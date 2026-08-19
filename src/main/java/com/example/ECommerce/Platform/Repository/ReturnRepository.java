package com.example.ECommerce.Platform.Repository;

import com.example.ECommerce.Platform.Model.OrderItem;
import com.example.ECommerce.Platform.Model.ReturnEntity;
import com.example.ECommerce.Platform.Model.ReturnStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnRepository extends JpaRepository<ReturnEntity,String> {
    List<ReturnEntity> findAllByOrderByRequestDateDesc();

    List<ReturnEntity> findByOrderItemIn(List<OrderItem> orderItems);

    long countByStatus(ReturnStatus returnStatus);

    ReturnEntity findByOrderItemOrderItemIdAndStatus(String orderItemId, ReturnStatus returnStatus);
}
