package com.example.ECommerce.Platform.DTO.OrderDTO;

import com.example.ECommerce.Platform.Model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetMyOrdersDTO {
    private String orderId;
    private LocalDateTime orderDate;

    private String productName;
    private int quantity;

    private OrderStatus status;
    private String statusMessage;
}
