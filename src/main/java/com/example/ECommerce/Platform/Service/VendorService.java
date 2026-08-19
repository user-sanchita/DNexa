package com.example.ECommerce.Platform.Service;

import com.example.ECommerce.Platform.DTO.AuthDTO.LoginRequest;
import com.example.ECommerce.Platform.DTO.AuthDTO.PasswordRequest;
import com.example.ECommerce.Platform.DTO.AuthDTO.PasswordResponse;
import com.example.ECommerce.Platform.DTO.CategoryDTO.MessageDTO;
import com.example.ECommerce.Platform.DTO.ResetTokenDTO.ForgotRequestDTO;
import com.example.ECommerce.Platform.DTO.ResetTokenDTO.ResetRequestDTO;
import com.example.ECommerce.Platform.DTO.ResetTokenDTO.ResetTokenResponseDTO;
import com.example.ECommerce.Platform.DTO.VendorDTO.*;
import com.example.ECommerce.Platform.Exception.*;
import com.example.ECommerce.Platform.Model.*;
import com.example.ECommerce.Platform.Repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Transactional
@Service
public class VendorService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ReturnRepository returnRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VendorResetTokenRepository vendorResetTokenRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private NotificationService notificationService;

    public  List<VendorResponseDTO> getProductsByVendor(String vendorId, String email) {

        vendorRepository.findByVendorIdAndVendorEmail(vendorId, email)
                .orElseThrow(() -> new NotFoundException("Vendor Not Found"));

        List<Product> products = productRepository.findAllByVendorVendorId(vendorId);

        return products.stream()
                .map(this::mapToVendorResponse)
                .toList();
    }

    private VendorResponseDTO mapToVendorResponse(Product product) {
        VendorResponseDTO responseDTO = new VendorResponseDTO();

        responseDTO.setProductId(product.getProductId());
        responseDTO.setProductName(product.getProductName());
        responseDTO.setDescription(product.getDescription());
        responseDTO.setPrice(product.getPrice());
        responseDTO.setSellPrice(product.getSellPrice());
        responseDTO.setStock(product.getStock());
        responseDTO.setDiscount(product.getDiscount());
        responseDTO.setActive(product.isActive());
        if (product.getCategory() != null) {
            responseDTO.setCategoryId(product.getCategory().getCategoryId());
            responseDTO.setCategoryName(product.getCategory().getCategoryName());
        }
        responseDTO.setReturnWindowInDays(product.getReturnWindowInDays());
        responseDTO.setWarrantyPeriod(product.getWarrantyPeriod());
        responseDTO.setCreatedAt(product.getCreatedAt());
        responseDTO.setUpdatedAt(product.getUpdatedAt());

        Double finalPrice = product.getSellPrice()*(1-product.getDiscount());
        responseDTO.setFinalPrice(finalPrice);

        return responseDTO;
    }

    public  List<VendorOrdersResponseDTO> getVendorOrders(String vendorId, String email) {
        vendorRepository.findByVendorIdAndVendorEmail(vendorId, email)
                .orElseThrow(() -> new NotFoundException("Vendor Not Found"));

        List<Product> products = productRepository.findAllByVendorVendorId(vendorId);
        if(products.isEmpty()) throw new NotFoundException("No Products Found for this Vendor");

        List<OrderItem> orderItems = orderItemRepository.findByProductIn(products);
        return orderItems.stream()
                .map(this::mapToVendorOrderResponse)
                .toList();

    }

    private VendorOrdersResponseDTO mapToVendorOrderResponse(OrderItem orderItem) {

        VendorOrdersResponseDTO responseDTO = new VendorOrdersResponseDTO();

        responseDTO.setOrderId(orderItem.getOrders().getOrderId());
        responseDTO.setOrderItemId(orderItem.getOrderItemId());
        responseDTO.setProductId(orderItem.getProduct().getProductId());
        responseDTO.setProductName(orderItem.getProduct().getProductName());
        responseDTO.setQuantity(orderItem.getQuantity());

        responseDTO.setPrice(orderItem.getPrice());
        responseDTO.setDiscount(orderItem.getDiscount());
        responseDTO.setSellPrice(orderItem.getSellPrice());
        responseDTO.setFinalPrice(orderItem.getFinalPrice());
        responseDTO.setTotalPrice(orderItem.getTotalPrice());

        responseDTO.setCustomerName(orderItem.getOrders().getUser().getUserName());

        Payment payment =orderItem.getOrders().getPayment();
        if(payment!=null) responseDTO.setPaymentStatus(payment.getPaymentStatus());
        responseDTO.setOrderStatus(orderItem.getOrderStatus());
        responseDTO.setOrderDate(orderItem.getOrders().getOrderDate());

        return responseDTO;
    }

    public  List<VendorReturnResponseDTO> getVendorReturns(String vendorId, String email) {
        vendorRepository.findByVendorIdAndVendorEmail(vendorId,email)
                .orElseThrow(()->new NotFoundException("Vendor Not Found"));

        List<Product> products = productRepository.findAllByVendorVendorId(vendorId);
        if(products.isEmpty()) throw new NotFoundException("No Products Found for this Vendor");

        List<OrderItem> orderItems = orderItemRepository.findByProductIn(products);
        if(orderItems.isEmpty()) throw new NotFoundException("No Orders Found");

        List<ReturnEntity> returnEntities = returnRepository.findByOrderItemIn(orderItems);
        if(returnEntities.isEmpty()) throw new NotFoundException("No Return Requests Found");

        return returnEntities.stream()
                .map(this::mapToVendorReturnResponse)
                .toList();

    }

    private VendorReturnResponseDTO mapToVendorReturnResponse(ReturnEntity returnEntity) {
        VendorReturnResponseDTO responseDTO = new VendorReturnResponseDTO();

        responseDTO.setReturnId(returnEntity.getReturnId());
        responseDTO.setOrderId(returnEntity.getOrderItem().getOrders().getOrderId());
        responseDTO.setOrderItemId(returnEntity.getOrderItem().getOrderItemId());
        responseDTO.setProductId(returnEntity.getOrderItem().getProduct().getProductId());
        responseDTO.setProductName(returnEntity.getOrderItem().getProduct().getProductName());
        responseDTO.setQuantity(returnEntity.getReturnQty());
        responseDTO.setCustomerName(returnEntity.getOrderItem().getOrders().getUser().getUserName());
        responseDTO.setReturnReason(returnEntity.getReason());
        responseDTO.setReturnDescription(returnEntity.getDescription());
        responseDTO.setReturnStatus(returnEntity.getStatus());
        responseDTO.setReturnRequestDate(returnEntity.getRequestDate());
        responseDTO.setFinalPrice(returnEntity.getOrderItem().getFinalPrice());
        responseDTO.setRefundAmount(returnEntity.getRefundAmount());

        return responseDTO;
    }

    public RegisterResponseVendor registerVendor(@Valid RegisterVendor registerVendor) {

        if(vendorRepository.existsByVendorEmail(registerVendor.getVendorEmail()))
            throw new AlreadyDoneException("Email Already Exist");

        Vendor vendor = Vendor.builder()
                .vendorName(registerVendor.getVendorName())
                .vendorEmail(registerVendor.getVendorEmail())
                .password(passwordEncoder.encode(registerVendor.getPassword()))
                .contactNumber(registerVendor.getContactNumber())
                .shopName(registerVendor.getShopName())
                .shopDescription(registerVendor.getShopDescription())
                .gstNumber(registerVendor.getGstNumber())
                .panNumber(registerVendor.getPanNumber())
                .bankAccountNo(registerVendor.getBankAccountNo())
                .bankName(registerVendor.getBankName())
                .ifscCode(registerVendor.getIfscCode())
                .accountHolderName(registerVendor.getAccountHolderName())
                .pickupAddress(registerVendor.getPickupAddress())
                .city(registerVendor.getCity())
                .state(registerVendor.getState())
                .pinCode(registerVendor.getPinCode())
                .role(UserRole.VENDOR)
                .build();

        Vendor saved = vendorRepository.save(vendor);
        return mapToRegisterResponse(saved);
    }

    private RegisterResponseVendor mapToRegisterResponse(Vendor saved) {

        RegisterResponseVendor responseVendor = new RegisterResponseVendor();

        responseVendor.setVendorId(saved.getVendorId());
        responseVendor.setVendorName(saved.getVendorName());
        responseVendor.setVendorEmail(saved.getVendorEmail());
        responseVendor.setContactNumber(saved.getContactNumber());
        responseVendor.setShopName(saved.getShopName());
        responseVendor.setShopDescription(saved.getShopDescription());
        responseVendor.setPickupAddress(saved.getPickupAddress());
        responseVendor.setCity(saved.getCity());
        responseVendor.setState(saved.getState());
        responseVendor.setPinCode(saved.getPinCode());
        responseVendor.setActive(saved.isActive());
        responseVendor.setVerified(saved.isVerified());

        return responseVendor;
    }

    public  RegisterResponseVendor updateVendorProfile(@Valid UpdateVendorDTO updateVendorDTO,String email) {
        Vendor vendor = vendorRepository.findByVendorEmail(email);
        if(vendor==null) throw new NotFoundException("Vendor Not Found");

        if(updateVendorDTO.getVendorName()!=null) vendor.setVendorName(updateVendorDTO.getVendorName());
        if(updateVendorDTO.getContactNumber()!=null) vendor.setContactNumber(updateVendorDTO.getContactNumber());
        if(updateVendorDTO.getShopName()!=null) vendor.setShopName(updateVendorDTO.getShopName());
        if(updateVendorDTO.getShopDescription()!=null) vendor.setShopDescription(updateVendorDTO.getShopDescription());

        if(updateVendorDTO.getPickupAddress()!=null) vendor.setPickupAddress(updateVendorDTO.getPickupAddress());
        if(updateVendorDTO.getCity()!=null) vendor.setCity(updateVendorDTO.getCity());
        if(updateVendorDTO.getState()!=null) vendor.setState(updateVendorDTO.getState());
        if(updateVendorDTO.getPinCode()!=null) vendor.setPinCode(updateVendorDTO.getPinCode());

        if(updateVendorDTO.getAccountNumber()!=null) vendor.setBankAccountNo(updateVendorDTO.getAccountNumber());
        if(updateVendorDTO.getBankName()!=null) vendor.setBankName(updateVendorDTO.getBankName());
        if(updateVendorDTO.getIfscCode()!=null) vendor.setIfscCode(updateVendorDTO.getIfscCode());
        if(updateVendorDTO.getAccountHolderName()!=null) vendor.setAccountHolderName(updateVendorDTO.getAccountHolderName());

        if(updateVendorDTO.getGstNumber()!=null) vendor.setGstNumber(updateVendorDTO.getGstNumber());
        if(updateVendorDTO.getPanNumber()!=null) vendor.setPanNumber(updateVendorDTO.getPanNumber());

        Vendor saved = vendorRepository.save(vendor);
        return mapToRegisterResponse(saved);
    }


    public  PasswordResponse changeVendorPassword(@Valid PasswordRequest passwordRequest, String email) {
        Vendor vendor = vendorRepository.findByVendorEmail(email);
        if(vendor==null) throw new NotFoundException("Vendor Not Found");

        if(!passwordEncoder.matches(passwordRequest.getOldPass(),vendor.getPassword()))
            throw new InvalidCredentialsException("Old Password is incorrect");
        if(passwordEncoder.matches(passwordRequest.getNewPass(), vendor.getPassword()))
            throw new SameException("New Password can't be same as Old Password");

        vendor.setPassword(passwordEncoder.encode(passwordRequest.getNewPass()));

        vendorRepository.save(vendor);

        return new PasswordResponse("Password Updated Successfully");
    }

    public @Nullable ResetTokenResponseDTO forgotVendorPassword(ForgotRequestDTO forgotRequestDTO) {
        Vendor vendor = vendorRepository.findByVendorEmail(forgotRequestDTO.getEmail());
        if(vendor==null) throw new NotFoundException("Vendor not found");

        vendorResetTokenRepository.deleteByVendor(vendor);
        String token = generateOTP();

        VendorResetToken resetToken = VendorResetToken.builder()
                .token(token)
                .vendor(vendor)
                .expiryTime(LocalDateTime.now().plusMinutes(2))
                .build();
        vendorResetTokenRepository.save(resetToken);
        emailService.sendOtp(vendor.getVendorEmail(), token);
        return new ResetTokenResponseDTO("Token is successfully generated",resetToken.getExpiryTime());
    }
    public String generateOTP() {
        int otp = new Random().nextInt(900000) + 100000;
        return String.valueOf(otp);
    }

    public @Nullable ResetTokenResponseDTO resetVendorPassword(@Valid ResetRequestDTO resetRequestDTO) {
        VendorResetToken resetToken = vendorResetTokenRepository.findByToken(resetRequestDTO.getToken());
        if(resetToken==null) throw new InvalidTokenException("Invalid Token");

        if(resetToken.getExpiryTime().isBefore(LocalDateTime.now())){
            vendorResetTokenRepository.delete(resetToken);
            throw new InvalidTokenException("Invalid Token");
        }
        Vendor vendor= resetToken.getVendor();

        if(passwordEncoder.matches( resetRequestDTO.getPassword(),vendor.getPassword()))
            throw new SameException("New Password cannot be same as old password");
        vendor.setPassword(passwordEncoder.encode(resetRequestDTO.getPassword()));
        vendorRepository.save(vendor);

        vendorResetTokenRepository.deleteByVendor(vendor);
        return new ResetTokenResponseDTO("Password is changed successfully",LocalDateTime.now());
    }

    public  MessageDTO vendorUpdateOrderStatus(String orderItemId, OrderStatus status, String email) {

        Vendor vendor = vendorRepository.findByVendorEmail(email);
        if(vendor==null) throw new NotFoundException("Vendor Not Found");

        OrderItem orderItem = orderItemRepository.findByOrderItemIdAndProductVendorVendorEmail(orderItemId,email)
                .orElseThrow(()->new NotFoundException("OrderItem Not Found For this Vendor"));

        Product product = orderItem.getProduct();
        if(product==null) throw new NotFoundException("Product Not Found");

        Orders order = orderItem.getOrders();
        if(order==null) throw new NotFoundException("Order Not Found");

        User user = order.getUser();
        if(user==null) throw new NotFoundException("User Not Found");

        if(status==OrderStatus.SHIPPED){
            if(!order.getOrderStatus().equals(OrderStatus.CONFIRMED))
                throw new FollowedByAnotherStatusException("Order have to be Confirmed");
            if(orderItem.getDeliveredAt()!=null)
                throw new FollowedByAnotherStatusException("Delivered item can't be Shipped.");
            if(orderItem.getCancelledAt()!=null)
                throw new FollowedByAnotherStatusException("Cancelled item can't be Shipped.");
            orderItem.setOrderStatus(OrderStatus.SHIPPED);
            orderItem.setShippedAt(LocalDateTime.now());
            orderItemRepository.save(orderItem);
            return new MessageDTO("OrderStats Updated into Shipped state.");
        }
        else if(status==OrderStatus.CANCELLED){
            if(!order.getOrderStatus().equals(OrderStatus.CONFIRMED))
                throw new FollowedByAnotherStatusException("Order have to be Confirmed.");
            if(orderItem.getShippedAt()!=null)
                throw new FollowedByAnotherStatusException("Delivered item can't be Shipped.");
            if(orderItem.getDeliveredAt()!=null)
                throw new FollowedByAnotherStatusException("Delivered item can't be Cancelled.");
            orderItem.setOrderStatus(OrderStatus.CANCELLED);
            orderItem.setCancelledAt(LocalDateTime.now());
            orderItemRepository.save(orderItem);

            Payment payment = order.getPayment();
            if(payment==null) throw new NotFoundException("Payment Not Found");

            notificationService.createNotification(
                    user,
                    "Order Cancelled",
                    "Unfortunately, " + vendor.getShopName() +
                            " could not process your order for '" +
                            product.getProductName() + "'."
            );

            if(!payment.getPaymentMethod().equals("COD")){
                notificationService.createNotification(
                        user,
                        "Order Cancelled",
                                " Your refund is being processed for "+
                                        product.getProductName() +" and will be credited to your original payment method soon."
                );
                paymentService.refundAmount(payment.getPaymentId(),payment.getTransactionId(),orderItem);
            }
            return new MessageDTO("OrderStats Updated into Cancelled state.");

        }
        return new MessageDTO("Choose OrderStatus SHIPPED/CANCELLED");
    }

    public RegisterResponseVendor approveOrRejectVendor(
            String vendorId,
            boolean approve) {

        Vendor vendor = vendorRepository.findByVendorId(vendorId)
                .orElseThrow(() ->
                        new NotFoundException("Vendor Not Found"));
        if(vendor.isActive() && vendor.isVerified()) throw new AlreadyDoneException("Vendor is already active and verified");

        if (approve) {

            vendor.setVendorStatus(VendorStatus.APPROVED);
            vendor.setVerified(true);
            vendor.setActive(true);

        } else {

            vendor.setVendorStatus(VendorStatus.REJECTED);
            vendor.setVerified(false);
            vendor.setActive(false);

        }
        Vendor saved = vendorRepository.save(vendor);

        return mapToRegisterResponse(saved);
    }
    public Vendor authenticate(LoginRequest loginRequest){

        Vendor vendor = vendorRepository
                .findByVendorEmail(loginRequest.getEmail());

        if(vendor == null)
            throw new NotFoundException("Vendor not found");


        if(!passwordEncoder.matches(
                loginRequest.getPassword(),
                vendor.getPassword()
        )){
            throw new InvalidCredentialsException("Invalid credentials");
        }


        if(!vendor.isVerified() || !vendor.isActive()){
            throw new InvalidCredentialsException(
                    "Vendor account is not approved yet"
            );
        }


        return vendor;
    }
}


