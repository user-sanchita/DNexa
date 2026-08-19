package com.example.ECommerce.Platform.DTO.OrderDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderNowRequest {
    private String productId;
    private Integer quantity;
    private String paymentMethod;
    private String addressId;
}
