package com.example.ECommerce.Platform.DTO.OrderDTO;

import com.example.ECommerce.Platform.Model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderItemDTO {
    private String productName;
    private String vendorName;
    private int quantity;
    private Double unitPrice;
    private Double totalPrice;
    private String statusMessage;
}
