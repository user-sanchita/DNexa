package com.example.ECommerce.Platform.DTO.NotificationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {

    private String notificationId;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
}
