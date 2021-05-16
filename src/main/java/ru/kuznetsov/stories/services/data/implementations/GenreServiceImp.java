package ru.kuznetsov.stories.services.data.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kuznetsov.stories.dao.GenreDao;
import ru.kuznetsov.stories.models.Genre;
import ru.kuznetsov.stories.security.exceptions.ValidationException;
import ru.kuznetsov.stories.services.data.interfaces.GenreService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GenreServiceImp implements GenreService {

    private GenreDao genreDao;

    @Autowired
    public GenreServiceImp(GenreDao genreDao){
        this.genreDao = genreDao;
    }
    @Override
    public List<Genre> getAllGenres() {
        return genreDao.findAll();
    }

    @Override
    public Genre getById(Long id) {
        return genreDao.getOne(id);
    }

    @Override
    public Genre addGenre(String genreName) {
        validateName(genreName);
        Genre genre = new Genre();
        genreName = genreName.substring(0,1).toUpperCase() + genreName.substring(1).toLowerCase();
        genre.setName(genreName);
        return genreDao.save(genre);
    }

    private void validateName(String genreName){
        if(!genreName.matches("^[а-яА-Я]+$")){
            throw new ValidationException("Название жанра должно состоять только из букв русского алфавита");
        };
    }
}
