package com.example.ECommerce.Platform.DTO.OrderDTO;

import com.example.ECommerce.Platform.DTO.CartDTO.ItemsResponseDTO;
import com.example.ECommerce.Platform.Model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetOrderByIdDTO {
    private String productId;
    private String productName;
    private int quantity;
    private OrderStatus orderStatus;
    private LocalDateTime date;
    private String deliveryAddress;
    private double disPrice;
    private double totalProductPrice;
}
