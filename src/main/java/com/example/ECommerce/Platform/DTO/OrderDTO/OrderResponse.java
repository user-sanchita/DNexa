package com.example.ECommerce.Platform.DTO.OrderDTO;

import com.example.ECommerce.Platform.Model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String orderId;
    private Double totalSellPrice;
    private Double totalDiscountedPrice;
    private Double orderTotalPrice;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private String paymentMethod;
}
