package com.example.ECommerce.Platform.Service;

import com.example.ECommerce.Platform.DTO.CategoryDTO.MessageDTO;
import com.example.ECommerce.Platform.DTO.OrderDTO.OrderResponse;
import com.example.ECommerce.Platform.DTO.PaymentDTO.AdminPaymentResponse;
import com.example.ECommerce.Platform.Exception.*;
import com.example.ECommerce.Platform.Model.*;
import com.example.ECommerce.Platform.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;

    public  OrderResponse paymentSuccess(String paymentId, String transacId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()->new NotFoundException("Payment not found"));

        if(payment.getPaymentStatus()== PaymentStatus.SUCCESS)
            throw new AlreadyDoneException("Payment Already Successful");
        if(paymentRepository.existsByTransactionId(transacId))
            throw new AlreadyDoneException("Transaction ID already exists");


        Orders order = payment.getOrder();
        if(order==null) throw new NotFoundException("Order Not Found");
        User user = order.getUser();
        if(user==null) throw new NotFoundException("User Not Found");

        for (OrderItem orderItem : order.getOrderItems()) {

            Product product = orderItem.getProduct();
            if(product==null){
                throw new NotFoundException("Product Not Found");
            }

            if (product.getStock() < orderItem.getQuantity())
                throw new ProductNotAvailableException(
                        "Only " + product.getStock()
                                + " items available for "
                                + product.getProductName());
        }

        order.setOrderStatus(
                OrderStatus.CONFIRMED
        );

        order.setConfirmedAt(
                LocalDateTime.now()
        );
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setTransactionId(transacId);

        Cart cart = cartRepository.findByUserUserId(order.getUser().getUserId());
        if(cart != null) cart.getCartItems().clear();

        for(OrderItem orderItem : order.getOrderItems()){
            Product product = orderItem.getProduct();
            product.setStock(product.getStock()-orderItem.getQuantity());
            orderItem.setOrderStatus(OrderStatus.CONFIRMED);
        }
        notificationService.createNotification(
                user,
                "Payment Successful",
                "Your payment has been completed."
        );
        notificationService.createNotification(
                user,
                "Order Placed",
                "Your order has been placed successfully."
        );

        return mapToOrderResponse(order);
    }
    private OrderResponse mapToOrderResponse(Orders savedOrder) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderDate(savedOrder.getOrderDate());
        orderResponse.setOrderId(savedOrder.getOrderId());
        orderResponse.setStatus(savedOrder.getOrderStatus());
        orderResponse.setOrderTotalPrice(savedOrder.getOrderTotalPrice());
        orderResponse.setTotalSellPrice(savedOrder.getTotalSellPrice());
        orderResponse.setTotalDiscountedPrice(savedOrder.getTotalDiscountedPrice());
        orderResponse.setPaymentMethod(savedOrder.getPayment().getPaymentMethod());
        return orderResponse;
    }

    public  OrderResponse paymentSuccessNow(String paymentId, String transacId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()->new NotFoundException("Payment Not Found"));

        if(payment.getPaymentStatus()==PaymentStatus.SUCCESS)
            throw new AlreadyDoneException("Payment Already Successful");
        if(paymentRepository.existsByTransactionId(transacId))
            throw new AlreadyDoneException("Transaction ID already exists");

        Orders order = payment.getOrder();
            if(order==null) throw new NotFoundException("Order Not Found");
            User user = order.getUser();
            if(user==null) throw new NotFoundException("User Not Found");
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            throw new NotFoundException("OrderItem not found");
        }
        OrderItem orderItem = order.getOrderItems().get(0);

        Product product = orderItem.getProduct();
        if(product==null) throw new NotFoundException("Product Not Found");
        if(product.getStock() < orderItem.getQuantity())
            throw new ProductNotAvailableException("Only " + product.getStock()
                    + " items available for "
                    + product.getProductName());

        order.setOrderStatus(
                OrderStatus.CONFIRMED
        );

        order.setConfirmedAt(
                LocalDateTime.now()
        );
        orderItem.setOrderStatus(OrderStatus.CONFIRMED);


        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setTransactionId(transacId);


        product.setStock(product.getStock()-orderItem.getQuantity());

        notificationService.createNotification(
                user,
                "Payment Successful",
                "Your payment has been completed."
        );
        notificationService.createNotification(
                user,
                "Order Placed",
                "Your order has been placed successfully."
        );

        return mapToOrderResponse(order);
    }

    public MessageDTO refundAmount(String paymentId, String transactionId,OrderItem orderItem) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found."));


        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {throw new FollowedByAnotherStatusException(
                    "Only successful payments can be refunded.");
        }

        if (payment.getTransactionId() == null || !payment.getTransactionId().equals(transactionId)) {
            throw new InvalidInputException("Invalid transaction id.");
        }


        orderItem.setOrderStatus(OrderStatus.REFUND_INITIATED);
        orderItem.setRefundInitiatedAt(LocalDateTime.now());
        orderItemRepository.save(orderItem);

        return new MessageDTO("Refund initiated successfully.");
    }


    public MessageDTO refundInBank(String bankAccountNo, String ifscCode,OrderItem orderItem) {

        if (bankAccountNo == null || bankAccountNo.isBlank()) {
            throw new InvalidInputException("Bank account number is required.");
        }

        if (ifscCode == null || ifscCode.isBlank()) {
            throw new InvalidInputException("IFSC code is required.");
        }

        if (bankAccountNo.length() < 9 || bankAccountNo.length() > 18) {
            throw new InvalidInputException("Invalid bank account number.");
        }

        if (ifscCode.length() != 11) {
            throw new InvalidInputException("Invalid IFSC code.");
        }
        orderItem.setOrderStatus(OrderStatus.REFUND_INITIATED);
        orderItem.setRefundInitiatedAt(LocalDateTime.now());
        orderItemRepository.save(orderItem);

        return new MessageDTO("Refund amount will be credited to your " + "bank account within 3-7 working days.");
    }

    public  AdminPaymentResponse getPaymentByOrderId(String orderId, String email) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(()->new NotFoundException("Order Not Found"));

        validateAdmin(email);

        Payment payment = paymentRepository.findByOrderOrderId(orderId);
        if(payment==null) throw new NotFoundException("Payment Not Found");

        return mapToPaymentResponse(payment);
    }

    private AdminPaymentResponse mapToPaymentResponse(Payment payment) {
        AdminPaymentResponse response = new AdminPaymentResponse();

        User user = payment.getOrder().getUser();

        response.setPaymentId(payment.getPaymentId());
        response.setUserId(user.getUserId());
        response.setUserName(user.getUserName());
        response.setEmail(user.getUserEmail());
        response.setOrderId(payment.getOrder().getOrderId());
        response.setTotalAmount(payment.getTotalAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setPaymentStatus(payment.getPaymentStatus());
        response.setTransactionId(payment.getTransactionId());
        response.setPaymentDate(payment.getPaymentDate());

        return response;
    }

    public  List<AdminPaymentResponse> getAllPayments(String email) {

        validateAdmin(email);

        List<Payment> payments = paymentRepository.findAll();

        return payments.stream()
                .map(this::mapToPaymentResponse)
                .toList();
    }

    public  AdminPaymentResponse getPaymentById(String paymentId, String email) {
        validateAdmin(email);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()->new NotFoundException("Payment Not Found"));
        return mapToPaymentResponse(payment);
    }

    private void validateAdmin(String email) {
        User user = userRepository.findByUserEmail(email);

        if (user == null) throw new NotFoundException("Admin Not Found");

        if (user.getRole() != UserRole.ADMIN) throw new UnAuthorizedException("Access Denied");
    }

    public MessageDTO refundComplete(String orderItemId, String email) {

        validateAdmin(email);

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException("Order item not found."));

        if (orderItem.getOrderStatus() != OrderStatus.REFUND_INITIATED) {
            throw new FollowedByAnotherStatusException(
                    "Refund has not been initiated for this order item."
            );
        }

        orderItem.setOrderStatus(OrderStatus.REFUNDED);
        orderItem.setRefundAt(LocalDateTime.now());

        orderItemRepository.save(orderItem);

        User user = orderItem.getOrders().getUser();

        notificationService.createNotification(
                user,
                "Refund Successful",
                "Your refund has been completed successfully."
        );

        return new MessageDTO("Refund completed successfully.");
    }
}
