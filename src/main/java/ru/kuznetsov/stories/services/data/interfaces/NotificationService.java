package ru.kuznetsov.stories.services.data.interfaces;

import org.springframework.stereotype.Component;
import ru.kuznetsov.stories.dto.NotificationDto;
import ru.kuznetsov.stories.models.Notification;
import ru.kuznetsov.stories.models.User;

import java.util.List;

@Component
public interface NotificationService {
    void sendNotification(User user, String theme, String text);
    List<NotificationDto> getNotifications(User user);
    Long getAmountOfNewNotifications(User user);
    NotificationDto getNotification(User user, Long id);
}
