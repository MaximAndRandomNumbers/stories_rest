package ru.kuznetsov.stories.models;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import ru.kuznetsov.stories.dto.StoryDto;

import javax.persistence.*;
import java.security.Principal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(type="text")
    private String fullText;

    private String title;

    @Type(type="text")
    private String shortDesc;

    @Column(name="is_approved")
    private boolean isApproved;

    @Column(name="on_moderation")
    private boolean onModeration;

    @Column(name="on_refactoring")
    private boolean onRefactoring;

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    private Double rating;

    @Column(name="marks_amount")
    private Long amountOfMarks;

    @Column(name="publish_date")
    @Temporal(TemporalType.DATE)
    private java.util.Date publishDate;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany
    @JoinTable(name="story_genre",
            joinColumns = {@JoinColumn(name="story_id")},
            inverseJoinColumns = {@JoinColumn(name="genre_id")})
    private Set<Genre> genres = new HashSet<>();

    @OneToMany(mappedBy = "story")
    private Set<UserStoryMark> marks = new HashSet<>();

    @OneToOne(mappedBy = "story")
    private ModeratedStory moderatedStory;

    public Story(String title, String shortDesc, String fullText, User author){
        this.title = title;
        this.shortDesc = shortDesc;
        this.fullText = fullText;
        this.author = author;
    }

    public Story(StoryDto storyDto){
        this.title = storyDto.getTitle();
        this.shortDesc = storyDto.getShortDesc();
        this.fullText = storyDto.getFullText();
    }

}
