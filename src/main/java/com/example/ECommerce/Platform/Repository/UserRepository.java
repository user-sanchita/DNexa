package com.example.ECommerce.Platform.Repository;

import com.example.ECommerce.Platform.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    User findByUserEmail(String email);

    Page<User> findByUserName(Pageable pageable, String userName);

    boolean existsByUserEmail(String email);


    Optional<User> findByUserIdAndUserEmail(String userId, String email);
}
