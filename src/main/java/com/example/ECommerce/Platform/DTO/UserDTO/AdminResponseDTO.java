package com.example.ECommerce.Platform.DTO.UserDTO;

import com.example.ECommerce.Platform.Model.UserRole;
import com.example.ECommerce.Platform.Model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseDTO {
    private String userId;
    private String userName;
    private String email;
    private String phone;
    private String gender;
    private UserRole role;
    private UserStatus status;
}
