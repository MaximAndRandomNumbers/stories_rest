package ru.kuznetsov.stories.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kuznetsov.stories.dao.GenreDao;
import ru.kuznetsov.stories.models.Genre;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenreDto {
    private Long id;
    private String name;

    public GenreDto(Genre genre){
        this.id = genre.getId();
        this.name = genre.getName();
    }
}
