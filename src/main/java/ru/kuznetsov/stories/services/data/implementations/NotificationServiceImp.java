package ru.kuznetsov.stories.services.data.implementations;

import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kuznetsov.stories.dao.NotificationDao;
import ru.kuznetsov.stories.dto.NotificationDto;
import ru.kuznetsov.stories.models.Notification;
import ru.kuznetsov.stories.models.User;
import ru.kuznetsov.stories.security.exceptions.AccessDeniedException;
import ru.kuznetsov.stories.services.data.interfaces.NotificationService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImp implements NotificationService {
    private final NotificationDao notificationDao;

    @Autowired
    public NotificationServiceImp(NotificationDao notificationDao) {
        this.notificationDao = notificationDao;
    }

    @Override
    public void sendNotification(User user, String theme, String text) {
        Notification notification = new Notification(user, theme, text);
        notification.setDate(new Date());
        notification.setRead(false);
        notificationDao.save(notification);
    }

    @Override
    public List<NotificationDto> getNotifications(User user) {
        List<Notification> notifications = notificationDao.findAllByUserOrderByIdDesc(user);
        if(notifications == null){
            return null;
        }
        return notifications.stream()
                .map(NotificationDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public Long getAmountOfNewNotifications(User user) {
        return notificationDao.getAmountOfNew(user.getId());
    }

    @Override
    public NotificationDto getNotification(User user, Long id) {
        Notification notification = notificationDao.getById(id).orElseThrow(IllegalArgumentException::new);
        if(!notification.getUser().getId().equals(user.getId())){
            throw new AccessDeniedException("Access denied");
        }
        notification.setRead(true);
        notificationDao.save(notification);
        return new NotificationDto(notification);
    }

}
