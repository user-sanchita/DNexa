package com.example.ECommerce.Platform.DTO.CartDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartGetDTO {
    private List<ItemsResponseDTO> cartItems;
    private int totalItems;
    private int totalQuantity;
    private double totalPrice;
    private double totalDiscount;
    private double orderTotalPrice;
}
