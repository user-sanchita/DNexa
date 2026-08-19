package com.example.ECommerce.Platform.Service;

import com.example.ECommerce.Platform.DTO.CategoryDTO.MessageDTO;
import com.example.ECommerce.Platform.DTO.ReturnDTO.AdminGetResponseDTO;
import com.example.ECommerce.Platform.DTO.ReturnDTO.AdminReviewRequestDTO;
import com.example.ECommerce.Platform.DTO.ReturnDTO.ReturnRequestDTO;
import com.example.ECommerce.Platform.DTO.ReturnDTO.VendorDisputeRequest;
import com.example.ECommerce.Platform.Exception.*;
import com.example.ECommerce.Platform.Model.*;
import com.example.ECommerce.Platform.Repository.OrderItemRepository;
import com.example.ECommerce.Platform.Repository.ReturnRepository;
import com.example.ECommerce.Platform.Repository.UserRepository;
import com.example.ECommerce.Platform.Repository.VendorRepository;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReturnService {

    @Autowired
    private ReturnRepository returnRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private PaymentService paymentService;

    public @Nullable MessageDTO returnRequest(ReturnRequestDTO requestDTO, String email) {
        User user = userRepository.findByUserEmail(email);
        if(user==null) throw new NotFoundException("User not found");
        UserRole role = user.getRole();
        if (!role.equals(UserRole.USER)) throw new UnAuthorizedException("Access Denied");

        OrderItem orderItem = orderItemRepository.findById(requestDTO.getOrderItemId())
                .orElseThrow(()->new NotFoundException("OrderItem not found"));
        if(requestDTO.getReturnQty() <= 0)
            throw new InvalidInputException("Return quantity must be greater than zero");
        if (orderItem.getDeliveredAt() == null)
            throw new FollowedByAnotherStatusException("Order must be delivered before creating a return request!");
        ReturnEntity existingReturn = returnRepository.findByOrderItemOrderItemIdAndStatus(orderItem.getOrderItemId(), ReturnStatus.PENDING);
        if (existingReturn != null)
            throw new AlreadyDoneException("A return request for this order item is already pending");
        int qty = requestDTO.getReturnQty() + orderItem.getReturnQty();
        if(qty>orderItem.getQuantity())
            throw new AlreadyDoneException("Return Quantity is Wrong or Product may be fully returned");
        LocalDateTime returnDeadLine = orderItem.getDeliveredAt().plusDays(orderItem.getProduct().getReturnWindowInDays());
        if(LocalDateTime.now().isAfter(returnDeadLine))
            throw new AlreadyDoneException("The return window for this product has already expired!");


        ReturnEntity myReturnEntity = new ReturnEntity();

        String paymentMethod = orderItem.getOrders().getPayment().getPaymentMethod();
        if(paymentMethod.equalsIgnoreCase("COD")){

            if (!"BANK".equalsIgnoreCase(requestDTO.getRefundType())) {
                throw new InvalidInputException(
                        "Cash on Delivery orders can only be refunded to a bank account"
                );
            }

            myReturnEntity.setRefundType("BANK");

            if ("BANK".equalsIgnoreCase(requestDTO.getRefundType())) {
                if (requestDTO.getBankAccountNo() == null || requestDTO.getIfscCode() == null || requestDTO.getAccountHolderName() == null) {
                    throw new IllegalArgumentException("Bank details are required when choosing Bank Refund.");
                }

                myReturnEntity.setBankAccountNo(requestDTO.getBankAccountNo());
                myReturnEntity.setBankName(requestDTO.getBankName());
                myReturnEntity.setIfscCode(requestDTO.getIfscCode());
                myReturnEntity.setAccountHolderName(requestDTO.getAccountHolderName());
            }
        }
        else myReturnEntity.setRefundType("ORIGINAL_SOURCE");
        myReturnEntity.setRequestDate(LocalDateTime.now());
        myReturnEntity.setOrderItem(orderItem);
        myReturnEntity.setReason(requestDTO.getReason());
        myReturnEntity.setStatus(ReturnStatus.PENDING);
        myReturnEntity.setReturnQty(requestDTO.getReturnQty());
        myReturnEntity.setRefundAmount(orderItem.getFinalPrice()*requestDTO.getReturnQty());


        returnRepository.save(myReturnEntity);
        return new MessageDTO("Return Requested Successfully");
    }
    //    //    //   // Admin Review Return Request  //   //   //   //

    public @Nullable MessageDTO adminReview(AdminReviewRequestDTO adminReviewRequestDTO,String email) {
        User user = userRepository.findByUserEmail(email);
        if(user==null) throw new NotFoundException("Admin Not Found");
        if(!user.getRole().equals(UserRole.ADMIN) && !user.getRole().equals(UserRole.SUPER_ADMIN))
            throw new UnAuthorizedException("Access Denied");

        ReturnEntity returns = returnRepository.findById(adminReviewRequestDTO.getReturnId())
                .orElseThrow(()->new NotFoundException("Return Item Not Found"));

        OrderItem orderItem = returns.getOrderItem();
        if(orderItem==null) throw new NotFoundException("OrderItem Not Found");
        Orders order = orderItem.getOrders();
        if(order==null) throw new NotFoundException("Order Not Found");

        if (returns.getStatus() == ReturnStatus.APPROVED || returns.getStatus() == ReturnStatus.REJECTED) {
            throw new AlreadyDoneException("This return request has already been processed");
        }
        if (adminReviewRequestDTO.getAdminComment() != null) {
            returns.setAdminComment(adminReviewRequestDTO.getAdminComment());
        }
        if(adminReviewRequestDTO.getStatus()==ReturnStatus.REJECTED) {
            returns.setStatus(ReturnStatus.REJECTED);
            orderItem.setOrderStatus(OrderStatus.DELIVERED);
            returnRepository.save(returns);
            return new MessageDTO("Return request has been rejected");
        }

        int qty = returns.getReturnQty()+ orderItem.getReturnQty();
        if(qty>orderItem.getQuantity()){
            returns.setStatus(ReturnStatus.REJECTED);
            return new MessageDTO("Return request rejected due to invalid quantity");
        }

        returns.setStatus(ReturnStatus.APPROVED);
        orderItem.setReturnQty(qty);

        returnRepository.save(returns);
        orderItemRepository.save(orderItem);

        Payment payment = order.getPayment();
        if(!payment.getPaymentMethod().equalsIgnoreCase("COD"))
            paymentService.refundAmount(payment.getPaymentId(), payment.getTransactionId(),orderItem);

        else{
            if(returns.getBankAccountNo()!=null) paymentService.refundInBank(returns.getBankAccountNo(),returns.getIfscCode(),orderItem);
        }
        Product product = orderItem.getProduct();
        product.setStock(product.getStock() + returns.getReturnQty());
        return new MessageDTO("Return is approved and get your money within 3 days");
    }

    //    //    //   // Vendor Raise Dispute  //   //   //   //

    public MessageDTO vendorRaiseDispute(VendorDisputeRequest vendorDisputeRequest, String email) {
        Vendor vendor = vendorRepository.findByVendorEmail(email);
        if(vendor==null) throw new NotFoundException("Vendor Not Found");
        if(!vendor.getRole().equals(UserRole.VENDOR)) throw new UnAuthorizedException("Access Denied");

        ReturnEntity returns = returnRepository.findById(vendorDisputeRequest.getReturnId())
                .orElseThrow(() -> new NotFoundException("Return Request Not Found"));
        Product product = returns.getOrderItem().getProduct();
        if (!product.getVendor().getVendorId().equals(vendor.getVendorId())) {
            throw new UnAuthorizedException("You can only raise disputes for your own products");
        }
        if (returns.getStatus() != ReturnStatus.PENDING) {
            throw new AlreadyDoneException("This return is already processed");
        }
        returns.setStatus(ReturnStatus.DISPUTED);
        returns.setAdminComment("Vendor Dispute: " + vendorDisputeRequest.getVendorReason() + " | Proof: " + vendorDisputeRequest.getProofImageUrl());

        returnRepository.save(returns);
        return new MessageDTO("Dispute raised successfully. Admin will review the proof.");
    }

    public @Nullable List<AdminGetResponseDTO> getAllReturns(String email) {
        User user = userRepository.findByUserEmail(email);
        if(user==null) throw new NotFoundException("Admin Not Found");

        if(!user.getRole().equals(UserRole.ADMIN) && !user.getRole().equals(UserRole.SUPER_ADMIN))
            throw new UnAuthorizedException("Access Denied");

        List<ReturnEntity> returnEntities = returnRepository.findAllByOrderByRequestDateDesc();
        return returnEntities.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AdminGetResponseDTO mapToResponse(ReturnEntity returns) {
        AdminGetResponseDTO responseDTO = new AdminGetResponseDTO();

        responseDTO.setReturnId(returns.getReturnId());
        responseDTO.setReturnStatus(returns.getStatus());
        responseDTO.setReason(returns.getReason());
        responseDTO.setAdminComment(returns.getAdminComment());
        responseDTO.setReturnRequestDate(returns.getRequestDate());

        if(returns.getOrderItem()==null) throw new NotFoundException("OrderItem Not Found");
        if(returns.getOrderItem().getOrders()==null) throw new NotFoundException("Order Not Found");
        User user = returns.getOrderItem().getOrders().getUser();
        if(user==null) throw new NotFoundException("User Not Found");
        responseDTO.setUserId(user.getUserId());
        responseDTO.setCustomerName(user.getUserName());
        responseDTO.setCustomerEmail(user.getUserEmail());

        responseDTO.setOrderItemId(returns.getOrderItem().getOrderItemId());

        Product product = returns.getOrderItem().getProduct();
        if(product==null) throw new NotFoundException("Product Not Found");
        responseDTO.setProductId(product.getProductId());
        responseDTO.setProductName(product.getProductName());
        responseDTO.setQuantity(returns.getReturnQty());
        responseDTO.setTotalPrice(returns.getRefundAmount());

        return  responseDTO;
    }
}
