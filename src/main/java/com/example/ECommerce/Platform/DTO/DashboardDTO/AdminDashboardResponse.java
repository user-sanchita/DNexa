package com.example.ECommerce.Platform.DTO.DashboardDTO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResponse {

    private long totalUsers;
    private long totalVendors;
    private long totalProducts;
    private long totalOrders;
    private long totalPayments;
    private long pendingReturns;
}
