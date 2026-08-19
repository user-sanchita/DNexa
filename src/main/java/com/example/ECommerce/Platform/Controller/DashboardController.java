package com.example.ECommerce.Platform.Controller;

import com.example.ECommerce.Platform.DTO.DashboardDTO.AdminDashboardResponse;
import com.example.ECommerce.Platform.DTO.DashboardDTO.UserDashboardResponse;
import com.example.ECommerce.Platform.DTO.DashboardDTO.VendorDashboardResponse;
import com.example.ECommerce.Platform.Service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/admin")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard(Authentication authentication) {
        return ResponseEntity.ok(
                dashboardService.getAdminDashboard(authentication.getName())
        );
    }

    @GetMapping("/vendor")
    public ResponseEntity<VendorDashboardResponse> getVendorDashboard(Authentication authentication) {

        return ResponseEntity.ok(
                dashboardService.getVendorDashboard(authentication.getName())
        );
    }

    @GetMapping("/user")
    public ResponseEntity<UserDashboardResponse> getUserDashboard(Authentication authentication) {

        return ResponseEntity.ok(
                dashboardService.getUserDashboard(authentication.getName())
        );
    }
}
