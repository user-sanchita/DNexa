package com.example.ECommerce.Platform.DTO.ReturnDTO;


import com.example.ECommerce.Platform.Model.ReturnReason;
import com.example.ECommerce.Platform.Model.ReturnStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminGetResponseDTO {
    // Return Info
    private String returnId;
    private ReturnStatus returnStatus;
    private ReturnReason reason;
    private String adminComment;
    private LocalDateTime returnRequestDate;

    // Customer Info
    private String userId;
    private String customerName;
    private String customerEmail;

    // Product & Order Item Info
    private String orderItemId;
    private String productId;
    private String productName;
    private int quantity;
    private double totalPrice;
}
