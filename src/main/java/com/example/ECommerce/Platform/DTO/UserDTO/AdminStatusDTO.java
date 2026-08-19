package com.example.ECommerce.Platform.DTO.UserDTO;

import com.example.ECommerce.Platform.Model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatusDTO {
    private String userId;
    private UserStatus status;
}

