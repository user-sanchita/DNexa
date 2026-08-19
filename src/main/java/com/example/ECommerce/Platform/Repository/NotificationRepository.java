package com.example.ECommerce.Platform.Repository;

import com.example.ECommerce.Platform.Model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,String> {

    List<Notification> findAllByUserUserEmailOrderByCreatedAtDesc(String email);
}
