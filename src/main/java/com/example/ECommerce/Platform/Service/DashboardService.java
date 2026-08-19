package com.example.ECommerce.Platform.Service;

import com.example.ECommerce.Platform.DTO.DashboardDTO.AdminDashboardResponse;
import com.example.ECommerce.Platform.DTO.DashboardDTO.UserDashboardResponse;
import com.example.ECommerce.Platform.DTO.DashboardDTO.VendorDashboardResponse;
import com.example.ECommerce.Platform.Exception.NotFoundException;
import com.example.ECommerce.Platform.Exception.UnAuthorizedException;
import com.example.ECommerce.Platform.Model.*;
import com.example.ECommerce.Platform.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DashboardService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private ReturnRepository returnRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;


    public UserDashboardResponse getUserDashboard(String email) {
        User user = userRepository.findByUserEmail(email);
        if(user==null) throw new NotFoundException("User Not Found");
        if(user.getRole()!=UserRole.USER) throw new UnAuthorizedException("Access Denied");

        return UserDashboardResponse.builder()
                .totalOrders(
                        orderRepository.countByUserUserId(user.getUserId())
                )
                .wishlistItems(
                        wishlistRepository.countByUserUserId(user.getUserId())
                )
                .cartItems(
                        cartItemRepository.countByCartUserUserId(user.getUserId())
                )
                .build();
    }

    public VendorDashboardResponse getVendorDashboard(String email) {
        Vendor vendor = vendorRepository.findByVendorEmail(email);
        if(vendor==null) throw new NotFoundException("Vendor Not Found");

        vendorRepository.findByVendorId(vendor.getVendorId())
                .orElseThrow(() -> new NotFoundException("Vendor Not Found"));

        return VendorDashboardResponse.builder()
                .totalProducts(
                        productRepository.countByVendorVendorId(vendor.getVendorId())
                )
                .totalOrders(
                        orderItemRepository.countByProductVendorVendorId(vendor.getVendorId())
                )
                .pendingOrders(
                        orderItemRepository.countByProductVendorVendorIdAndOrderStatus(
                                vendor.getVendorId(),
                                OrderStatus.PENDING
                        )
                )
                .deliveredOrders(
                        orderItemRepository.countByProductVendorVendorIdAndOrderStatus(
                                vendor.getVendorId(),
                                OrderStatus.DELIVERED
                        )
                )
                .build();
    }

    public AdminDashboardResponse getAdminDashboard(String email) {
        User user = userRepository.findByUserEmail(email);
        if(user==null) throw new NotFoundException("User Not Found");
        if(user.getRole()!= UserRole.ADMIN && user.getRole()!=UserRole.SUPER_ADMIN)
            throw new UnAuthorizedException("Access Denied");

        return AdminDashboardResponse.builder()
                .totalUsers(userRepository.count())
                .totalVendors(vendorRepository.count())
                .totalProducts(productRepository.count())
                .totalOrders(orderRepository.count())
                .totalPayments(paymentRepository.count())
                .pendingReturns(
                        returnRepository.countByStatus(ReturnStatus.PENDING)
                )
                .build();
    }
}

