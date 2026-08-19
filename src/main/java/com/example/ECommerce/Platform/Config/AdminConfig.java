package com.example.ECommerce.Platform.Config;

import com.example.ECommerce.Platform.Model.User;
import com.example.ECommerce.Platform.Model.UserRole;
import com.example.ECommerce.Platform.Model.UserStatus;
import com.example.ECommerce.Platform.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminConfig {

    @Bean
    public CommandLineRunner createSuperAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String email = "sanchitakoley16102005@gmail.com";

            if (!userRepository.existsByUserEmail(email)) {

                User superAdmin = User.builder()
                        .userName("Super Admin")
                        .userEmail(email)
                        .password(
                                passwordEncoder.encode("SuperAdmin@123")
                        )
                        .phone("9732705070")
                        .gender("FEMALE")
                        .status(UserStatus.ACTIVE)
                        .role(UserRole.SUPER_ADMIN)
                        .build();

                userRepository.save(superAdmin);
            }
        };
    }
}
