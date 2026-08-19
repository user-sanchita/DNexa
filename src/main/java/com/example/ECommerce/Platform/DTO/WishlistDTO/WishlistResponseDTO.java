package com.example.ECommerce.Platform.DTO.WishlistDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponseDTO {
    private String productId;
    private String productName;
    private Double finalPrice;//after discount
}
