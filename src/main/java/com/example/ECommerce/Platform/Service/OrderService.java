package com.example.ECommerce.Platform.Service;

import com.example.ECommerce.Platform.DTO.CartDTO.ItemsResponseDTO;
import com.example.ECommerce.Platform.DTO.CategoryDTO.MessageDTO;
import com.example.ECommerce.Platform.DTO.OrderDTO.*;
import com.example.ECommerce.Platform.DTO.PaymentDTO.PaymentResponse;
import com.example.ECommerce.Platform.Exception.*;
import com.example.ECommerce.Platform.Model.*;
import com.example.ECommerce.Platform.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private NotificationService notificationService;

    //    //    //   // Place order from cart   //  //  //  //
    public Object placeOrder(OrderRequest orderRequest,String email) {
        User user = userRepository.findByUserEmail(email);
        if(user==null) throw new NotFoundException("User Not Found");

        Cart cart = cartRepository.findByUserUserEmail(email);
        if (cart == null || cart.getCartItems().isEmpty())
            throw new NotFoundException("Cart is empty");

        Address address = addressRepository.findByAddressIdAndUserUserEmail(orderRequest.getAddressId(), user.getUserEmail())
                .orElseThrow(()->new NotFoundException("Address Not Found"));
        Orders order = Orders.builder()
                .user(user)
                .orderStatus(OrderStatus.PENDING)
                .build();
        List<OrderItem> orderItems = new ArrayList<>();
        double totalSellPrice = 0.0, totalDiscountedPrice = 0.0, orderTotalPrice = 0.0;
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            if (!product.isActive()) {
                throw new NotActivatedException(
                        product.getProductName() + "Product is inactive"
                );
            }
            if(!isCategoryActive(product.getCategory()))throw new NotActivatedException("Category is inactive");
            if (product.getStock() < cartItem.getQuantity()) {
                throw new ProductNotAvailableException(
                        "Only " + product.getStock() +
                                " items available for " +
                                product.getProductName()
                );
            }
            double price = product.getPrice();
            float discount = product.getDiscount();
            double discountedPrice = product.getSellPrice() * product.getDiscount();//single product e discounted Price
            double totalDisPrice = cartItem.getQuantity() * discountedPrice;
            double finalPrice = product.getSellPrice() - discountedPrice;//single product er price after discount
            double totalPrice = cartItem.getQuantity() * finalPrice;

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .orders(order)
                    .price(price)
                    .discount(discount)
                    .discountedPrice(discountedPrice)
                    .totalDisPrice(totalDisPrice)
                    .finalPrice(finalPrice)
                    .totalPrice(totalPrice)
                    .sellPrice(product.getSellPrice())//single product er sell price
                    .totalSellPrice(cartItem.getQuantity() * product.getSellPrice())//qty*sellPrice
                    .returnWindowInDays(product.getReturnWindowInDays())
                    .warrantyPeriod(product.getWarrantyPeriod())
                    .build();
            orderItems.add(orderItem);
            totalSellPrice += cartItem.getQuantity() * product.getSellPrice();
            totalDiscountedPrice += totalDisPrice;
            orderTotalPrice += totalPrice;
        }
        order.setOrderItems(orderItems);
        order.setTotalSellPrice(totalSellPrice);
        order.setTotalDiscountedPrice(totalDiscountedPrice);
        order.setOrderTotalPrice(orderTotalPrice);
        order.setDeliveryAddress(mapToDeliveryAddress(address));
        Orders savedOrder = orderRepository.save(order);

        Payment payment = Payment.builder()
                .order(savedOrder)
                .paymentMethod(orderRequest.getPaymentMethod())
                .totalAmount(savedOrder.getOrderTotalPrice())
                .build();

        if (orderRequest.getPaymentMethod().equalsIgnoreCase("COD")) {
            savedOrder.setOrderStatus(OrderStatus.CONFIRMED);
            savedOrder.setConfirmedAt(
                    LocalDateTime.now()
            );// r save korte lagbe na repo te because @Transactional er moddhe hochche hibernate automatically korbe

            for (OrderItem orderItem : savedOrder.getOrderItems()) {
                Product product = orderItem.getProduct();
                product.setStock(product.getStock() - orderItem.getQuantity());
                productRepository.save(product);
                orderItem.setOrderStatus(OrderStatus.CONFIRMED);
            }
            cart.getCartItems().clear();// r save korte lagbe na repo te because @Transactional er moddhe hochche hibernate automatically korbe
            cartRepository.save(cart);
            payment.setPaymentStatus(PaymentStatus.PENDING);
            Payment savedPayment = paymentRepository.save(payment);
            savedOrder.setPayment(savedPayment);
            notificationService.createNotification(
                    user,
                    "Order Placed",
                    "Your order has been placed successfully."
            );
            orderRepository.flush();
            return mapToOrderResponse(savedOrder);
        }

        payment.setPaymentStatus(PaymentStatus.INITIATED);
        Payment savedPayment = paymentRepository.save(payment);
        savedOrder.setPayment(savedPayment);
        return new PaymentResponse(savedPayment.getPaymentId());
    }

    private DeliveryAddress mapToDeliveryAddress(Address address) {
        DeliveryAddress deliveryAddress = new DeliveryAddress();

        deliveryAddress.setFullName(address.getFullName());
        deliveryAddress.setMobileNo(address.getMobileNo());
        deliveryAddress.setAlternateMobileNumber(address.getAlternateMobileNumber());
        deliveryAddress.setStreetAddress(address.getStreetAddress());
        deliveryAddress.setCity(address.getCity());
        deliveryAddress.setState(address.getState());
        deliveryAddress.setPinCode(address.getPinCode());
        deliveryAddress.setLandmark(address.getLandmark());
        deliveryAddress.setDistrict(address.getDistrict());
        deliveryAddress.setCountry(address.getCountry());

        return deliveryAddress;
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

    //    //    //   // Order direct from product  //  //  //  //
    public Object orderNow(OrderNowRequest orderNowRequest,String email) {
        User user = userRepository.findByUserEmail(email);
        if(user==null) throw new NotFoundException("User Not Found");

        Product product = productRepository.findById(orderNowRequest.getProductId())
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        if (!product.isActive()) throw new NotActivatedException("Product is inactive");
        if(!isCategoryActive(product.getCategory())) throw new NotActivatedException("Category is inactive");
        if (product.getStock() < orderNowRequest.getQuantity())
            throw new ProductNotAvailableException("Only " + product.getStock() + " product is available");

        Address address = addressRepository.findByAddressIdAndUserUserEmail(orderNowRequest.getAddressId(), user.getUserEmail())
                .orElseThrow(()->new NotFoundException("Address Not Found"));

        double price = product.getPrice();
        float discount = product.getDiscount();
        double totalSellPrice = orderNowRequest.getQuantity() * product.getSellPrice();
        double disPrice = product.getDiscount() * product.getSellPrice();
        double totalDiscountedPrice = orderNowRequest.getQuantity() * disPrice;
        double finalPrice = product.getSellPrice() - disPrice;
        double orderTotalPrice = orderNowRequest.getQuantity() * finalPrice;

        Orders order = Orders.builder()
                .orderStatus(OrderStatus.PENDING)
                .user(user)
                .totalSellPrice(totalSellPrice)
                .totalDiscountedPrice(totalDiscountedPrice)
                .orderTotalPrice(orderTotalPrice)
                .build();
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = OrderItem.builder()
                .orders(order)
                .quantity(orderNowRequest.getQuantity())
                .product(product)
                .price(price)
                .discount(discount)
                .sellPrice(product.getSellPrice())
                .totalSellPrice(totalSellPrice)
                .discountedPrice(disPrice)
                .totalDisPrice(totalDiscountedPrice)
                .finalPrice(finalPrice)
                .totalPrice(orderTotalPrice)
                .returnWindowInDays(product.getReturnWindowInDays())
                .warrantyPeriod(product.getWarrantyPeriod())
                .build();

        orderItems.add(orderItem);
        order.setOrderItems(orderItems);
        order.setDeliveryAddress(mapToDeliveryAddress(address));
        Orders savedOrder = orderRepository.save(order);

        Payment payment = Payment.builder()
                .order(savedOrder)
                .paymentMethod(orderNowRequest.getPaymentMethod())
                .totalAmount(savedOrder.getOrderTotalPrice())
                .build();

        if (orderNowRequest.getPaymentMethod().equalsIgnoreCase("COD")) {
            savedOrder.setOrderStatus(OrderStatus.CONFIRMED);
            savedOrder.setConfirmedAt(LocalDateTime.now());
            orderItem.setOrderStatus(OrderStatus.CONFIRMED);
            product.setStock(product.getStock() - orderNowRequest.getQuantity());
            productRepository.save(product);
            payment.setPaymentStatus(PaymentStatus.PENDING);
            Payment savedPayment = paymentRepository.save(payment);
            savedOrder.setPayment(savedPayment);
            notificationService.createNotification(
                    user,
                    "Order Placed",
                    "Your order has been placed successfully."
            );
            orderRepository.flush();
            System.out.println(savedOrder.getOrderDate());
            return mapToOrderResponse(savedOrder);
        }
        payment.setPaymentStatus(PaymentStatus.INITIATED);
        Payment savedPayment = paymentRepository.save(payment);
        savedOrder.setPayment(savedPayment);
        return new PaymentResponse(savedPayment.getPaymentId());
    }

    //    //    //   // Get My OrderItem By OrderItemId   //  //  //  //

    public GetOrderByIdDTO getMyOrderByOrderItemId(String orderItemId,String email) {
        OrderItem orderItem = orderItemRepository.findByOrderItemIdAndOrdersUserUserEmail(orderItemId,email)
                .orElseThrow(() -> new NotFoundException("OrderItem Not Found"));
        return mapToGetOrderByIdDTO(orderItem);
    }

    private GetOrderByIdDTO mapToGetOrderByIdDTO(OrderItem orderItem) {
        GetOrderByIdDTO dto = new GetOrderByIdDTO();
        dto.setProductId(orderItem.getProduct().getProductId());
        dto.setProductName(orderItem.getProduct().getProductName());
        dto.setQuantity(orderItem.getQuantity());
        OrderStatus status = orderItem.getOrderStatus();
        dto.setOrderStatus(status);

        LocalDateTime dynamicDate = switch (status) {
            case DELIVERED -> orderItem.getDeliveredAt();
            case CANCELLED -> orderItem.getCancelledAt();
            case SHIPPED -> orderItem.getShippedAt();
            case REFUNDED, PARTIAL_REFUNDED -> orderItem.getRefundAt();
            default -> orderItem.getOrders() != null ? orderItem.getOrders().getOrderDate() : null;
        };
        dto.setDate(dynamicDate);
        if (orderItem.getOrders() != null && orderItem.getOrders().getDeliveryAddress() != null) {
            DeliveryAddress addr = orderItem.getOrders().getDeliveryAddress();
            String fullAddress = addr.getStreetAddress() + ", " +
                    addr.getCity() + ", " +
                    addr.getState() + " - " +
                    addr.getPinCode();
            dto.setDeliveryAddress(fullAddress);
        }
        dto.setDisPrice(orderItem.getTotalDisPrice());
        dto.setTotalProductPrice(orderItem.getTotalPrice());
        return dto;
    }



//    //    //   // UPDATE STATUS    //  //  //  //

    public MessageDTO updateOrderStatus(String orderItemId, OrderStatus status,String email) {
        User user = userRepository.findByUserEmail(email);
        if (user == null) throw new NotFoundException("User Not Found");
        if (user.getRole() != UserRole.ADMIN &&
                user.getRole() != UserRole.SUPER_ADMIN) {
            throw new UnAuthorizedException("Access denied");
        }
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException("OrderItem Not Found"));
        Orders order = orderItem.getOrders();
        if(order==null) throw new NotFoundException("Order not found");
        if (orderItem.getOrderStatus() == status)
            throw new SameException("Order already in " + status + " status");

        switch (status) {
            case SHIPPED -> {//cancelled orderItem shipped hote pare na
                if (orderItem.getCancelledAt() != null)
                    throw new FollowedByAnotherStatusException("Cancelled order cannot be shipped");
                if(orderItem.getShippedAt()!=null)
                    throw new FollowedByAnotherStatusException("Order already shipped");
                orderItem.setShippedAt(LocalDateTime.now());
            }

            case DELIVERED -> {   //  shipped chara delivery na
                if (orderItem.getShippedAt() == null)
                    throw new FollowedByAnotherStatusException("Order must be shipped first");
                if (orderItem.getCancelledAt() != null)
                    throw new FollowedByAnotherStatusException("Cancelled order cannot be delivered");
                orderItem.setDeliveredAt(LocalDateTime.now());
                Payment payment = paymentRepository.findByOrderOrderId(order.getOrderId());
                payment.setPaymentStatus(PaymentStatus.SUCCESS);
                payment.setPaymentDate(LocalDateTime.now());
                paymentRepository.save(payment);
            }

            case CANCELLED -> {//  delivered order cancel na
                if (orderItem.getDeliveredAt() != null)
                    throw new FollowedByAnotherStatusException("Delivered order cannot be cancelled");
                Product product = orderItem.getProduct();
                product.setStock(product.getStock() + orderItem.getQuantity());


                Payment payment = order.getPayment();
                if (payment == null) throw new NotFoundException("Payment Not Found");
                productRepository.save(product);
                orderItem.setCancelledAt(LocalDateTime.now());

                if (!payment.getPaymentMethod().equalsIgnoreCase("COD")) {
                    paymentService.refundAmount(payment.getPaymentId(),payment.getTransactionId(),orderItem);
                }
            }

       }
        orderItem.setOrderStatus(status);
        orderItemRepository.save(orderItem);
        return new MessageDTO("Status Updated Successfully");
    }
    //    //    //   // Get My All Orders   //  //  //  //

    public List<GetMyOrdersDTO> getMyOrders(String email) {
        User user = userRepository.findByUserEmail(email);

        List<Orders> orders = orderRepository.findByUserUserIdOrderByOrderDateDesc(user.getUserId());
        List<OrderItem> items = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .toList();
        return items.stream()
                .map(this::mapToGetMyOrdersDTO)
                .toList();

    }

    private GetMyOrdersDTO mapToGetMyOrdersDTO(OrderItem item) {
        GetMyOrdersDTO dto = new GetMyOrdersDTO();
        dto.setOrderId(item.getOrders().getOrderId());
        dto.setOrderDate(item.getOrders().getOrderDate());
        dto.setProductName(item.getProduct().getProductName());
        dto.setQuantity(item.getQuantity());
        dto.setStatus(item.getOrderStatus());
        OrderStatus status = item.getOrderStatus();
        dto.setStatusMessage(
                switch (status) {
                    case DELIVERED -> "Delivered on " + item.getDeliveredAt();
                    case CANCELLED -> "Cancelled on " + item.getCancelledAt();
                    case SHIPPED -> "Shipped on " + item.getShippedAt();
                    case REFUNDED -> "Returned on " + item.getRefundAt();
                    case PARTIAL_REFUNDED ->
                            "Returned on " + item.getRefundAt() + ", Qty: " + item.getReturnQty() +
                                    " | Delivered on " + item.getDeliveredAt() + ", Qty: " + item.getQuantity();
                    default -> "Ordered on " + item.getOrders().getOrderDate();
                }
        );
        return dto;
    }

    //    //    //   // Admin Get Order By OrderId   //   //   //   //

    public AdminGetOrderByIdDTO AdminGetOrderById(String orderId,String email) {
        User user = userRepository.findByUserEmail(email);

        if (user == null)
            throw new NotFoundException("Admin not found");

        if (user.getRole() != UserRole.ADMIN &&
                user.getRole() != UserRole.SUPER_ADMIN) {
            throw new UnAuthorizedException("Access denied");
        }
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order Not Found"));
        return mapToAdminGetOrderById(order);
    }

    private AdminGetOrderByIdDTO mapToAdminGetOrderById(Orders order) {
        AdminGetOrderByIdDTO response = new AdminGetOrderByIdDTO();
        response.setOrderDate(order.getOrderDate());
        response.setOrderId(order.getOrderId());
        response.setUserId(order.getUser().getUserId());
        response.setUserEmail(order.getUser().getUserEmail());
        response.setUserName(order.getUser().getUserName());
        response.setPaymentMethod(order.getPayment().getPaymentMethod());
        response.setTotalSellPrice(order.getTotalSellPrice());
        response.setOrderTotalPrice(order.getOrderTotalPrice());

        List<OrderItem> orderItem = order.getOrderItems();
        List<AdminOrderItemDTO> items = orderItem.stream()
                .map(this::mapToAdminOrderItemDTO)
                .toList();
        response.setOrderItems(items);
        response.setTotalItems(orderItem.size());

        return response;
    }

    private AdminOrderItemDTO mapToAdminOrderItemDTO(OrderItem orderItem) {
        AdminOrderItemDTO response = new AdminOrderItemDTO();
        response.setProductName(orderItem.getProduct().getProductName());
        response.setVendorName(orderItem.getProduct().getVendor().getShopName());
        response.setUnitPrice(orderItem.getSellPrice());
        response.setTotalPrice(orderItem.getTotalPrice());
        response.setQuantity(orderItem.getQuantity());
        OrderStatus status = orderItem.getOrderStatus();
        response.setStatusMessage(switch (status) {
            case DELIVERED -> "Delivered on " + orderItem.getDeliveredAt();
            case CANCELLED -> "Cancelled on " + orderItem.getCancelledAt();
            case SHIPPED -> "Shipped on " + orderItem.getShippedAt();
            case REFUNDED -> "Returned on " + orderItem.getRefundAt();
            case PARTIAL_REFUNDED ->
                    "Returned on " + orderItem.getRefundAt() + ", Qty: " + orderItem.getReturnQty() +
                            " | Delivered on " + orderItem.getDeliveredAt() + ", Qty: " + orderItem.getQuantity();
            default -> "Ordered on " + orderItem.getOrders().getOrderDate();
        });

        return response;
    }

    //    //    //   // Cancelled order  //   //   //   //
    public MessageDTO cancelledOrder(String email, String orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException("OrderItem Not Found"));
        User user = userRepository.findByUserEmail(email);
        if(user==null) throw new NotFoundException("User Not Found");

        if (!orderItem.getOrders().getUser().getUserEmail().equals(email)) throw new UnAuthorizedException("You can't cancel this order");

        if (orderItem.getCancelledAt() != null) throw new AlreadyDoneException("Order already cancelled");
        if (orderItem.getDeliveredAt() != null)
            throw new FollowedByAnotherStatusException("Delivered order can't be cancelled");
        if(orderItem.getShippedAt()!=null) throw new FollowedByAnotherStatusException("After Shipping you can't cancelled your order");

        Product product = orderItem.getProduct();
        product.setStock(product.getStock() + orderItem.getQuantity());
        productRepository.save(product);

        Orders order = orderItem.getOrders();

        Payment payment = order.getPayment();
        if (payment == null) throw new NotFoundException("Payment Not Found");


       if (!payment.getPaymentMethod().equalsIgnoreCase("COD")) {
            paymentService.refundAmount(payment.getPaymentId(),payment.getTransactionId(),orderItem);
        }
        orderItem.setOrderStatus(OrderStatus.CANCELLED);
        orderItem.setCancelledAt(LocalDateTime.now());
        orderItemRepository.save(orderItem);
        return new MessageDTO("Order Cancelled Successfully");

    }
    //    //    //   // Admin Get ALL OrderS  //   //   //   //

    public List<AdminGetOrderByIdDTO> adminGetAllOrders(String email) {
        User user = userRepository.findByUserEmail(email);
        if (user == null) throw new NotFoundException("User Not Found");
        if (user.getRole() != UserRole.ADMIN &&
                user.getRole() != UserRole.SUPER_ADMIN) {
            throw new UnAuthorizedException("Access denied");
        }
        List<Orders> orders = orderRepository.findAll();
        return orders.stream().map(this::mapToAdminGetOrderById).toList();
    }

    public  boolean isCategoryActive(Category category) {
        while (category != null) {
            if (!category.isActive()) return false;
            category = category.getParentCategory();
        }
        return true;
    }
}


