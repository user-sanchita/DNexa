package com.example.ECommerce.Platform.DTO.AuthDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRegisterRequest {

    private String userName;

    @NotBlank(message = "Email can't be null")
    @Email(message = "Invalid Email")
    @Column(nullable = false,unique = true)
    private String email;
    @NotBlank(message = "Password can't be null")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
            message = "Password must be at least 8 characters long and " +
                    "include uppercase, lowercase, number and special character"
    )
    private String password;

    @Column(nullable = false,length=10)
    private String phone;

    private String gender;
}
