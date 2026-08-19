package com.example.ECommerce.Platform.DTO.PaymentDTO;

import com.example.ECommerce.Platform.Model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminPaymentResponse {

    private String paymentId;

    // User Details
    private String userId;
    private String userName;
    private String email;

    // Order Details
    private String orderId;

    // Payment Details
    private Double totalAmount;
    private String paymentMethod;
    private PaymentStatus paymentStatus;
    private String transactionId;
    private LocalDateTime paymentDate;
}
