package com.example.ECommerce.Platform.DTO.VendorDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorResponseDTO {
    private String productId;
    private String productName;
    private String description;
    private Double price;//cost
    private Double sellPrice;
    private Float discount;
    private Double finalPrice;//after discount
    private Integer stock;
    private boolean isActive;
    private String categoryId;
    private  String categoryName;
    private Integer returnWindowInDays;
    private String warrantyPeriod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
