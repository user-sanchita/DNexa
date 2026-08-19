package com.example.ECommerce.Platform.Repository;

import com.example.ECommerce.Platform.Model.OrderItem;
import com.example.ECommerce.Platform.Model.OrderStatus;
import com.example.ECommerce.Platform.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem,String> {

    List<OrderItem> findByProductIn(List<Product> products);

    Optional<OrderItem> findByOrderItemIdAndProductVendorVendorEmail(String orderItemId, String email);

    OrderItem findByOrderItemIdAndOrdersUserUserId(String orderItemId, String userId);

    long countByProductVendorVendorId(String vendorId);

    long countByProductVendorVendorIdAndOrderStatus(String vendorId, OrderStatus orderStatus);

    Optional<OrderItem> findByOrderItemIdAndOrdersUserUserEmail(String orderItemId, String email);
}
