package com.example.ECommerce.Platform.DTO.VendorDTO;

import com.example.ECommerce.Platform.Model.ReturnReason;
import com.example.ECommerce.Platform.Model.ReturnStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorReturnResponseDTO {
    private String returnId;

    private String orderId;
    private String orderItemId;

    private String productId;
    private String productName;

    private Integer quantity;

    private String customerName;

    private ReturnReason returnReason;
    private String returnDescription;

    private ReturnStatus returnStatus;

    private LocalDateTime returnRequestDate;

    private Double finalPrice;
    private Double refundAmount;

}
