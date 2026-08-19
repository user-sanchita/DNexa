package com.example.ECommerce.Platform.Repository;

import com.example.ECommerce.Platform.Model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart,String> {
    Cart findByUserUserEmail(String email);

    Cart findByUserUserId(String userId);
}
