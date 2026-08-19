package com.example.ECommerce.Platform.Controller;

import com.example.ECommerce.Platform.DTO.CategoryDTO.MessageDTO;
import com.example.ECommerce.Platform.DTO.OrderDTO.OrderResponse;
import com.example.ECommerce.Platform.DTO.PaymentDTO.AdminPaymentResponse;
import com.example.ECommerce.Platform.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/success")
    public ResponseEntity<OrderResponse> paymentSuccess(@RequestParam String paymentId, @RequestParam String transacId){
        return ResponseEntity.ok(paymentService.paymentSuccess(paymentId,transacId));
    }

    @PostMapping("/success/now")
    public ResponseEntity<OrderResponse> paymentSuccessNow(@RequestParam String paymentId, @RequestParam String transacId){
        return ResponseEntity.ok(paymentService.paymentSuccessNow(paymentId,transacId));
    }

    @GetMapping("/get/byOrderId")
    public ResponseEntity<AdminPaymentResponse> getPaymentByOrderId(@RequestParam String orderId,Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId,email));
    }

    @GetMapping("/get/allPayments")
    public ResponseEntity<List<AdminPaymentResponse>> getAllPayments(Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(paymentService.getAllPayments(email));
    }

    @GetMapping("/get/paymentId")
    public ResponseEntity<AdminPaymentResponse> getPaymentById(@RequestParam String paymentId, Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId,email));
    }
    @PostMapping("/refund/complete")
    public ResponseEntity<MessageDTO> refundComplete(@RequestParam String orderItemId, Authentication authentication) {

        String email = authentication.getName();
        return ResponseEntity.ok(paymentService.refundComplete(orderItemId, email));
    }

}
