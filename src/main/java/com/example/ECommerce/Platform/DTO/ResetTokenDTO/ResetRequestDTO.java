package com.example.ECommerce.Platform.DTO.ResetTokenDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ResetRequestDTO {
    private String token;

    @NotBlank(message = "Password can't be null")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
            message = "Password must be at least 8 characters long and " +
                    "include uppercase, lowercase, number and special character"
    )
    private String password;
}
