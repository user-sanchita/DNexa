package com.example.ECommerce.Platform.Controller;

import com.example.ECommerce.Platform.DTO.NotificationDTO.NotificationResponseDTO;
import com.example.ECommerce.Platform.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getNotifications(Authentication authentication){

        return ResponseEntity.ok(notificationService.getNotifications(authentication.getName()));
    }


    @PatchMapping("/read/{notificationId}")
    public ResponseEntity<String> markAsRead(@PathVariable String notificationId, Authentication authentication){

        return ResponseEntity.ok(notificationService.markAsRead(notificationId, authentication.getName())
        );
    }
}
