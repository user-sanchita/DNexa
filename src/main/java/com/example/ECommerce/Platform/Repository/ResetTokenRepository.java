package com.example.ECommerce.Platform.Repository;

import com.example.ECommerce.Platform.Model.ResetToken;
import com.example.ECommerce.Platform.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResetTokenRepository extends JpaRepository<ResetToken,String> {
    void deleteByUser(User user);
    ResetToken findByToken(String token);
}
