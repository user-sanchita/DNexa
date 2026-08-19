package com.example.ECommerce.Platform.DTO.VendorDTO;

import com.example.ECommerce.Platform.Model.OrderStatus;
import com.example.ECommerce.Platform.Model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorOrdersResponseDTO {
    private String orderId;
    private String orderItemId;

    private String productId;
    private String productName;

    private Integer quantity;

    private Double price;//cost price
    private Float discount;
    private Double sellPrice;//selling price
    private Double finalPrice;//after discount single product value
    private Double totalPrice;//qty* after Discounted price

    private String customerName;

    private PaymentStatus paymentStatus;
    private OrderStatus orderStatus;
    private LocalDateTime orderDate;
}
