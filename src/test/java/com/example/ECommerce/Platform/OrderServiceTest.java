package com.example.ECommerce.Platform;

import com.example.ECommerce.Platform.DTO.CategoryDTO.MessageDTO;
import com.example.ECommerce.Platform.Exception.FollowedByAnotherStatusException;
import com.example.ECommerce.Platform.Exception.NotFoundException;
import com.example.ECommerce.Platform.Exception.SameException;
import com.example.ECommerce.Platform.Exception.UnAuthorizedException;
import com.example.ECommerce.Platform.Model.*;
import com.example.ECommerce.Platform.Repository.OrderItemRepository;
import com.example.ECommerce.Platform.Repository.PaymentRepository;
import com.example.ECommerce.Platform.Repository.ProductRepository;
import com.example.ECommerce.Platform.Repository.UserRepository;
import com.example.ECommerce.Platform.Service.OrderService;
import com.example.ECommerce.Platform.Service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
     private OrderItemRepository orderItemRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private OrderService orderService;

// UPDATE ORDER_STATUS
    @Test
    void testUpdateOrderStatus_whenUserNotFound() {

        when(userRepository.findByUserEmail("wrong@gmail.com"))
                .thenReturn(null);

        assertThrows(NotFoundException.class, () ->
                orderService.updateOrderStatus(
                        "item-1",
                        OrderStatus.SHIPPED,
                        "wrong@gmail.com"
                ));

        verify(userRepository).findByUserEmail("wrong@gmail.com");
        verifyNoInteractions(orderItemRepository);
    }

    @Test
    void testUpdateOrderStatus_whenUserUnauthorized() {

        User user = new User();
        user.setRole(UserRole.USER);

        when(userRepository.findByUserEmail("user@gmail.com"))
                .thenReturn(user);

        assertThrows(UnAuthorizedException.class, () ->
                orderService.updateOrderStatus(
                        "item-1",
                        OrderStatus.SHIPPED,
                        "user@gmail.com"
                ));

        verify(userRepository).findByUserEmail("user@gmail.com");
        verifyNoInteractions(orderItemRepository);
    }

    @Test
    void testUpdateOrderStatus_whenOrderItemNotFound() {

        User user = new User();
        user.setRole(UserRole.ADMIN);

        when(userRepository.findByUserEmail("admin@gmail.com"))
                .thenReturn(user);

        when(orderItemRepository.findById("item-1"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                orderService.updateOrderStatus(
                        "item-1",
                        OrderStatus.SHIPPED,
                        "admin@gmail.com"
                ));

        verify(orderItemRepository).findById("item-1");
    }

    @Test
    void testUpdateOrderStatus_whenOrderNotFound() {

        User user = new User();
        user.setRole(UserRole.ADMIN);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrders(null);

        when(userRepository.findByUserEmail("admin@gmail.com"))
                .thenReturn(user);

        when(orderItemRepository.findById("item-1"))
                .thenReturn(Optional.of(orderItem));

        assertThrows(NotFoundException.class, () ->
                orderService.updateOrderStatus(
                        "item-1",
                        OrderStatus.SHIPPED,
                        "admin@gmail.com"
                ));
    }
    @Test
    void testUpdateOrderStatus_whenStatusAlreadySame() {

        User user = new User();
        user.setRole(UserRole.ADMIN);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderStatus(OrderStatus.SHIPPED);

        Orders order = new Orders();
        orderItem.setOrders(order);

        when(userRepository.findByUserEmail("admin@gmail.com"))
                .thenReturn(user);

        when(orderItemRepository.findById("item-1"))
                .thenReturn(Optional.of(orderItem));

        assertThrows(SameException.class, () ->
                orderService.updateOrderStatus(
                        "item-1",
                        OrderStatus.SHIPPED,
                        "admin@gmail.com"
                ));
    }

    @Test
    void testUpdateOrderStatus_whenShippedSuccessfully() {

        User user = new User();
        user.setRole(UserRole.ADMIN);

        Orders order = new Orders();

        OrderItem orderItem = new OrderItem();
        orderItem.setOrders(order);
        orderItem.setOrderStatus(OrderStatus.PENDING);
        orderItem.setCancelledAt(null);
        orderItem.setShippedAt(null);

        when(userRepository.findByUserEmail("admin@gmail.com"))
                .thenReturn(user);

        when(orderItemRepository.findById("item-1"))
                .thenReturn(Optional.of(orderItem));

        MessageDTO result = orderService.updateOrderStatus(
                "item-1",
                OrderStatus.SHIPPED,
                "admin@gmail.com"
        );

        assertNotNull(result);
        assertNotNull(orderItem.getShippedAt());
    }

    @Test
    void testUpdateOrderStatus_whenCancelledOrderShipped() {

        User user = new User();
        user.setRole(UserRole.ADMIN);

        Orders order = new Orders();

        OrderItem orderItem = new OrderItem();
        orderItem.setOrders(order);
        orderItem.setOrderStatus(OrderStatus.PENDING);
        orderItem.setCancelledAt(LocalDateTime.now());

        when(userRepository.findByUserEmail("admin@gmail.com"))
                .thenReturn(user);

        when(orderItemRepository.findById("item-1"))
                .thenReturn(Optional.of(orderItem));

        assertThrows(FollowedByAnotherStatusException.class, () ->
                orderService.updateOrderStatus(
                        "item-1",
                        OrderStatus.SHIPPED,
                        "admin@gmail.com"
                ));
    }

    @Test
    void testUpdateOrderStatus_whenNotShippedBeforeDelivery() {

        User user = new User();
        user.setRole(UserRole.ADMIN);

        Orders order = new Orders();

        OrderItem orderItem = new OrderItem();
        orderItem.setOrders(order);
        orderItem.setOrderStatus(OrderStatus.PENDING);
        orderItem.setShippedAt(null);

        when(userRepository.findByUserEmail("admin@gmail.com"))
                .thenReturn(user);

        when(orderItemRepository.findById("item-1"))
                .thenReturn(Optional.of(orderItem));

        assertThrows(FollowedByAnotherStatusException.class, () ->
                orderService.updateOrderStatus(
                        "item-1",
                        OrderStatus.DELIVERED,
                        "admin@gmail.com"
                ));
    }

    @Test
    void testUpdateOrderStatus_whenCancelledSuccessfully() {

        User user = new User();
        user.setRole(UserRole.ADMIN);

        Orders order = new Orders();

        Payment payment = new Payment();
        payment.setPaymentId("pay-1");
        payment.setTransactionId("txn-1");
        payment.setPaymentMethod("COD");

        order.setPayment(payment);

        Product product = new Product();
        product.setStock(10);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrders(order);
        orderItem.setOrderStatus(OrderStatus.PENDING);
        orderItem.setQuantity(2);
        orderItem.setProduct(product);
        orderItem.setDeliveredAt(null);

        when(userRepository.findByUserEmail("admin@gmail.com"))
                .thenReturn(user);

        when(orderItemRepository.findById("item-1"))
                .thenReturn(Optional.of(orderItem));

        MessageDTO result = orderService.updateOrderStatus(
                "item-1",
                OrderStatus.CANCELLED,
                "admin@gmail.com"
        );

        assertNotNull(result);

        assertEquals(12, product.getStock());
        assertNotNull(orderItem.getCancelledAt());

        verify(productRepository).save(product);

        verify(paymentService, never())
                .refundAmount(anyString(), anyString(), any(OrderItem.class));
    }

    @Test
    void testUpdateOrderStatus_whenCancelledNonCOD_shouldRefund() {

        User user = new User();
        user.setRole(UserRole.ADMIN);

        Orders order = new Orders();

        Payment payment = new Payment();
        payment.setPaymentId("pay-1");
        payment.setTransactionId("txn-1");
        payment.setPaymentMethod("UPI");

        order.setPayment(payment);

        Product product = new Product();
        product.setStock(10);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrders(order);
        orderItem.setOrderStatus(OrderStatus.PENDING);
        orderItem.setQuantity(2);
        orderItem.setProduct(product);

        when(userRepository.findByUserEmail("admin@gmail.com"))
                .thenReturn(user);

        when(orderItemRepository.findById("item-1"))
                .thenReturn(Optional.of(orderItem));

        orderService.updateOrderStatus(
                "item-1",
                OrderStatus.CANCELLED,
                "admin@gmail.com"
        );

        verify(paymentService).refundAmount(
                "pay-1",
                "txn-1",
                orderItem
        );
    }
}
