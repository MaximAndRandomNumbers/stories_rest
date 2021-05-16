package ru.kuznetsov.stories.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    private String text;
    private String theme;
    private Date date;

    @Column(name="is_read")
    private boolean isRead;

    public Notification(User user, String theme, String text){
        this.user = user;
        this.theme = theme;
        this.text = text;
    }
}
