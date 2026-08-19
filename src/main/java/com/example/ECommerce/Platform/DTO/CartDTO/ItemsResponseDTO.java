package com.example.ECommerce.Platform.DTO.CartDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemsResponseDTO {
    private String productId;
    private String productName;
    private int quantity;
    private double sellPrice;
}
