package ru.kuznetsov.stories.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kuznetsov.stories.models.Notification;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private String text;
    private String theme;
    private Date date;
    private boolean read;

    public NotificationDto(Notification notification){
        this.id = notification.getId();
        this.text = notification.getText();
        this.date = notification.getDate();
        this.read = notification.isRead();
        this.theme = notification.getTheme();
    }
}
