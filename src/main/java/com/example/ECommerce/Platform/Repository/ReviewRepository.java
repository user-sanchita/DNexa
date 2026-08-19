package com.example.ECommerce.Platform.Repository;

import com.example.ECommerce.Platform.Model.OrderItem;
import com.example.ECommerce.Platform.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review,String> {
    boolean existsByOrderItem(OrderItem orderItem);

    Review findByOrderItem(OrderItem orderItem);
}
