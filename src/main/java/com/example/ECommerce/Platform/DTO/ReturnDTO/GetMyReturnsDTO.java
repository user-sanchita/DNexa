package com.example.ECommerce.Platform.DTO.ReturnDTO;

import com.example.ECommerce.Platform.Model.OrderStatus;
import com.example.ECommerce.Platform.Service.OrderService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetMyReturnsDTO {
    private String productId;
    private String statusMess;
    private OrderStatus orderStatus;
    private Integer Qty;
}
