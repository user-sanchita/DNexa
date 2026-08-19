package com.example.ECommerce.Platform.DTO.ProductDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseAdmin {
    private String productId;
    private String productName;
    private Double price;
    private Integer stock;
    private String description;
    private Double sellPrice;
    private Float discount;
    private String categoryId;
    private String categoryName;
    private Integer returnWindowInDays;
    private String warrantyPeriod;
}
