package com.example.ECommerce.Platform.DTO.CartDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatequantityDTO {
    private String productId;
    private int qty;
}
