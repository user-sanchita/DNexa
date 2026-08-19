package com.example.ECommerce.Platform.Controller;

import com.example.ECommerce.Platform.DTO.CategoryDTO.MessageDTO;
import com.example.ECommerce.Platform.DTO.OrderDTO.*;
import com.example.ECommerce.Platform.Model.OrderStatus;
import com.example.ECommerce.Platform.Model.UserRole;
import com.example.ECommerce.Platform.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/place/orders")
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest orderRequest,Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.placeOrder(orderRequest,email));
    }

    @PostMapping("/order/now")
    public ResponseEntity<?> orderNow(@RequestBody OrderNowRequest orderNowRequest,Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.orderNow(orderNowRequest,email));
    }

    @GetMapping("/order/ById/{orderItemId}")
    public ResponseEntity<GetOrderByIdDTO> getMyOrderByOrderItemId(@PathVariable String orderItemId,Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.getMyOrderByOrderItemId(orderItemId,email));
    }

    @GetMapping("/my/orders")
    public ResponseEntity<List<GetMyOrdersDTO>> getMyOrders(Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.getMyOrders(email));
    }

    @PatchMapping("/admin/status/{orderItemId}")
    public ResponseEntity<MessageDTO> updateOrderStatus(@PathVariable String orderItemId, @RequestParam OrderStatus status,Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.updateOrderStatus(orderItemId, status,email));
    }

    @GetMapping("/admin/getOrder/ById/{orderId}")
    public ResponseEntity<AdminGetOrderByIdDTO> AdminGetOrderById(@PathVariable String orderId,Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.AdminGetOrderById(orderId,email));
    }

    @PatchMapping("/cancel/order")
    public ResponseEntity<MessageDTO> cancelledOrder(Authentication authentication, @RequestParam String orderItemId){
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.cancelledOrder(email,orderItemId));
    }

    @GetMapping("/adminGet/all/orders")
    public ResponseEntity<List<AdminGetOrderByIdDTO>> adminGetAllOrders(Authentication authentication){
        String email = authentication.getName();

        return ResponseEntity.ok(orderService.adminGetAllOrders(email));
    }

}
