package com.example.ECommerce.Platform.DTO.AuthDTO;

import com.example.ECommerce.Platform.Model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private String userid;

    private String username;

    private String email;

    private String phone;

}
