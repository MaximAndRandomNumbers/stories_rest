package ru.kuznetsov.stories.models;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="user_story_mark")
public class UserStoryMark {

    @EmbeddedId
    private UserStoryMarkPK id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @MapsId("storyId")
    @JoinColumn(name="story_id")
    private Story story;

    @Column(name="mark")
    private Integer mark;


    public UserStoryMark(User user, Story story, Integer mark) {
        this.id = new UserStoryMarkPK(user.getId(),story.getId());
        this.user = user;
        this.story = story;
        this.mark = mark;
    }

}
