package com.example.ECommerce.Platform.Repository;

import com.example.ECommerce.Platform.Model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<WishlistItem,String> {
    List<WishlistItem> findByUserUserId(String userId);

    long countByUserUserId(String userId);

    boolean existsByUserUserEmailAndProductProductId(String email, String productId);

    void deleteByUserUserEmailAndProductProductId(String email, String productId);
}
