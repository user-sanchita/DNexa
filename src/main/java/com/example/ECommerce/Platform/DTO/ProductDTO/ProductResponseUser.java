package com.example.ECommerce.Platform.DTO.ProductDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseUser {
    private String productId;
    private String productName;
    private String description;
    private Double sellPrice;
    private boolean inStock;
    private Float discount;
    private Double discountedPrice;
    private Double totalAmount;
}
