package com.example.ECommerce.Platform.Repository;

import com.example.ECommerce.Platform.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  PaymentRepository extends JpaRepository<Payment,String> {

    Payment findByOrderOrderId(String orderId);

    boolean existsByTransactionId(String transacId);
}
