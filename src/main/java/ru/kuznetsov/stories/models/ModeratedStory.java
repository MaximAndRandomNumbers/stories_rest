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
@Table(name="moderated_story")
public class ModeratedStory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="story_id")
    Long id;

    @OneToOne
    @JoinColumn(name = "story_id", nullable = false)
    Story story;

    @OneToOne
    @JoinColumn(name = "moderator_id", nullable = false)
    User moderator;

    @Column(name="started_date")
    Date startedDate;

    public ModeratedStory(Story story, User moderator, Date startedDate){
        this.story = story;
        this.moderator = moderator;
        this.startedDate = startedDate;
    }
}
