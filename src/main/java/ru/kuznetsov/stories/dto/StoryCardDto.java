package ru.kuznetsov.stories.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kuznetsov.stories.models.Genre;
import ru.kuznetsov.stories.models.Story;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoryCardDto {

    private Long id;
    private String title;
    private String shortDesc;
    private Double rating;
    private java.util.Date publishDate;
    private Long amountOfMarks;
    private Set<String> nameOfGenres;
    private String authorLogin;

    public StoryCardDto (Story story){
        this.id = story.getId();
        this.title = story.getTitle();
        this.shortDesc = story.getShortDesc();
        this.rating = story.getRating();
        this.publishDate = story.getPublishDate();
        this.amountOfMarks = story.getAmountOfMarks();
        this.authorLogin = story.getAuthor().getLogin();
        this.nameOfGenres = (story.getGenres()
                .stream()
                .map(Genre::getName)
                .collect(Collectors.toSet()));
    }
}
