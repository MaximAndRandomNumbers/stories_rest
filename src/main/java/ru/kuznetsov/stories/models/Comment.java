package ru.kuznetsov.stories.models;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private Date date;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="story_id")
    private Story story;

    public Comment(User user, Story story, String text){
        this.text = text;
        this.user = user;
        this.story = story;
    }

}
