package com.example.ECommerce.Platform.Service;

import com.example.ECommerce.Platform.DTO.NotificationDTO.NotificationResponseDTO;
import com.example.ECommerce.Platform.Exception.NotFoundException;
import com.example.ECommerce.Platform.Exception.UnAuthorizedException;
import com.example.ECommerce.Platform.Model.Notification;
import com.example.ECommerce.Platform.Model.User;
import com.example.ECommerce.Platform.Repository.NotificationRepository;
import com.example.ECommerce.Platform.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void createNotification(User user, String title, String message){

        Notification notification =
                Notification.builder()
                        .user(user)
                        .title(title)
                        .message(message)
                        .build();

        notificationRepository.save(notification);
    }


    public List<NotificationResponseDTO> getNotifications(String email){

        User user = userRepository.findByUserEmail(email);

        if(user == null) throw new NotFoundException("User Not Found");


        return notificationRepository
                .findAllByUserUserEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }


    public String markAsRead(String notificationId, String email){

        Notification notification = notificationRepository.findById(notificationId)
                        .orElseThrow(() -> new NotFoundException("Notification Not Found"));

        if(!notification.getUser()
                .getUserEmail()
                .equals(email)){

            throw new UnAuthorizedException(
                    "Access Denied"
            );
        }

        notification.setRead(true);

        notificationRepository.save(notification);

        return "Notification marked as read successfully.";
    }


    private NotificationResponseDTO mapToDTO(Notification notification){

        return new NotificationResponseDTO(
                notification.getNotificationId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
