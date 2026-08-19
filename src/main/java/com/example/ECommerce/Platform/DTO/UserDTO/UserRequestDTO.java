package com.example.ECommerce.Platform.DTO.UserDTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    private String userId;
    private String userName;
    @Column(length=10)
    private String phone;
    private String gender;
}
