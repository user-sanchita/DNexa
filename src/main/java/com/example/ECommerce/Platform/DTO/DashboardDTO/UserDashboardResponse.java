package com.example.ECommerce.Platform.DTO.DashboardDTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDashboardResponse {

    private long totalOrders;
    private long wishlistItems;
    private long cartItems;
}
