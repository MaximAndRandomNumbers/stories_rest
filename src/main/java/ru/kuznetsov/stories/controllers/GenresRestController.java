package ru.kuznetsov.stories.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kuznetsov.stories.dto.GenreDto;
import ru.kuznetsov.stories.models.Genre;
import ru.kuznetsov.stories.services.data.interfaces.GenreService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/genres")
public class GenresRestController {

    private final GenreService genreService;

    @Autowired
    public GenresRestController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("")
    public ResponseEntity<Set<GenreDto>> getGenres(){
        Set<GenreDto> genreDtos = genreService.getAllGenres().stream().map(GenreDto::new).collect(Collectors.toSet());
        return ResponseEntity.ok(genreDtos);
    }
}
