package ru.kuznetsov.stories.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kuznetsov.stories.models.Comment;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentDto {

    private Long id;
    private String text;
    private Date date;
    private String userLogin;
    private Long storyId;

    public CommentDto(Comment comment){
        this.date = comment.getDate();
        this.id = comment.getId();
        this.storyId = comment.getStory().getId();
        this.userLogin = comment.getUser().getLogin();
        this.text = comment.getText();
    }
}
