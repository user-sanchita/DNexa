package com.example.ECommerce.Platform.DTO.DashboardDTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorDashboardResponse {

    private long totalProducts;
    private long totalOrders;
    private long pendingOrders;
    private long deliveredOrders;
}
