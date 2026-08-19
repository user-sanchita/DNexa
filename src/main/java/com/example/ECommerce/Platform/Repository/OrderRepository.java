package com.example.ECommerce.Platform.Repository;

import com.example.ECommerce.Platform.Model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders,String> {
    List<Orders> findByUserUserIdOrderByOrderDateDesc(String userId);

    long countByUserUserId(String userId);
}
