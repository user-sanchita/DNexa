package com.example.ECommerce.Platform.Repository;

import com.example.ECommerce.Platform.Model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem,String> {

    List<CartItem> findAllByCartCartId(String cartId);

    long countByCartUserUserId(String userId);
}
