package com.example.ECommerce.Platform.DTO.AuthDTO;

import com.example.ECommerce.Platform.Model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String userName;

    private String email;

    private String password;

    private String phone;

    private String gender;
}
