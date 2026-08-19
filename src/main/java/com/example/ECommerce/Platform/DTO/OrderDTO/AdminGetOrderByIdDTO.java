package com.example.ECommerce.Platform.DTO.OrderDTO;

import com.example.ECommerce.Platform.DTO.CartDTO.ItemsResponseDTO;
import com.example.ECommerce.Platform.Model.OrderStatus;
import com.example.ECommerce.Platform.Model.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminGetOrderByIdDTO {

    private String orderId;

    private LocalDateTime orderDate;

    private List<AdminOrderItemDTO> orderItems;

    private String userId;

    private String userName;

    private String userEmail;

    private String paymentMethod;

    private double totalSellPrice;

    private double orderTotalPrice;

    private int totalItems;


}
