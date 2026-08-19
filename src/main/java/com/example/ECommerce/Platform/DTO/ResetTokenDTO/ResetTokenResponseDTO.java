package com.example.ECommerce.Platform.DTO.ResetTokenDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetTokenResponseDTO {
    private String message;
    private LocalDateTime timeStamp;
}
