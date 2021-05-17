package ru.kuznetsov.stories.services.data.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import ru.kuznetsov.stories.dao.StoryDao;
import ru.kuznetsov.stories.dto.FilterDto;
import ru.kuznetsov.stories.dto.StoryDto;
import ru.kuznetsov.stories.models.Genre;
import ru.kuznetsov.stories.models.Story;
import ru.kuznetsov.stories.security.exceptions.AccessDeniedException;
import ru.kuznetsov.stories.security.exceptions.ValidationException;
import ru.kuznetsov.stories.services.data.interfaces.UserService;
import ru.kuznetsov.stories.services.data.interfaces.GenreService;
import ru.kuznetsov.stories.services.data.interfaces.StoryService;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StoryServiceImp implements StoryService {
    private final StoryDao storyDao;
    private final GenreService genreService;
    private final UserService userService;

    @Value("${validation.max_length.short_desc}")
    private Long MAX_LENGTH_SHORT_DESC;

    @Value("${validation.max_length.title}")
    private Long MAX_LENGTH_TITLE;

    @Autowired
    public StoryServiceImp(StoryDao storyDao, GenreService genreService, UserService userService) {
        this.storyDao = storyDao;
        this.genreService = genreService;
        this.userService = userService;
    }

    @Override
    public void deleteStory(Long storyId, Principal principal) {
        Story story = storyDao.getOne(storyId);
        if(!story.getAuthor().getLogin().equals(principal.getName())){
            throw new AccessDeniedException("Нет прав на удаление");
        }
        storyDao.delete(story);
    }

    @Override
    public List<String> findStoriesByPartOfTitle(String partOfTitle) {
        return storyDao.findStoriesByPartOfTitle("%" + partOfTitle.toLowerCase() + "%");
    }

    @Override
    public List<String> findStoriesByPartOfAuthorLogin(String partOfAuthorLogin) {
        return storyDao.findStoriesByPartOfAuthorLogin("%" + partOfAuthorLogin.toLowerCase() + "%");
    }

    @Override
    public void save(StoryDto storyDto, Principal principal) {
        validateStory(storyDto);
        Story story = new Story(storyDto);
        Set<Genre> genres = storyDto.getGenresId().stream().map(genreService::getById).collect(Collectors.toSet());
        story.setGenres(genres);
        story.setAuthor(userService.findByLogin(principal.getName()));
        story.setRating(0d);
        story.setPublishDate(new Date());
        story.setAmountOfMarks(0L);
        storyDao.save(story);
    }

    @Override
    public void updateStory(StoryDto storyDto, Principal principal) {
        validateStory(storyDto);
        Story story = storyDao.getOne(storyDto.getId());
        if(!story.isOnRefactoring()
                || !story.getAuthor().getLogin().equals(principal.getName())){
            throw new ValidationException("Нет прав на изменение");
        }
        Set<Genre> genres = storyDto.getGenresId().stream().map(genreService::getById).collect(Collectors.toSet());
        story.setGenres(genres);
        story.setTitle(storyDto.getTitle());
        story.setFullText(storyDto.getFullText());
        story.setShortDesc(storyDto.getShortDesc());
        story.setPublishDate(new Date());
        story.setOnRefactoring(false);
        storyDao.save(story);
    }

    private void validateStory(StoryDto storyDto) {
        String title = storyDto.getTitle();
        if (title == null || title.equals("")) {
            throw new ValidationException("Введите название рассказа");
        }

        if(title.length() > MAX_LENGTH_TITLE){
            throw new ValidationException("Длина названия должна быть меньше " + MAX_LENGTH_TITLE + " символов");
        }
        String shortDesc = storyDto.getShortDesc();
        if (shortDesc == null || shortDesc.equals("")) {
            throw new ValidationException("Кратко опишите рассказ");
        }
        if(shortDesc.length() > MAX_LENGTH_SHORT_DESC){
            throw new ValidationException("Длина краткого описания должна быть меньше " + MAX_LENGTH_SHORT_DESC + " символов");
        }

        Set<Long> genresIds = storyDto.getGenresId();
        if (genresIds == null || genresIds.size() == 0) {
            throw new ValidationException("Выберите хотя бы 1 жанр");
        }

        String fullText = storyDto.getFullText();
        if (fullText == null) {
            throw new ValidationException("Введите текст рассказа");
        }
    }

    @Override
    public void updateRating(Long storyId, Double newRating, Long newAmountOfMarks) {
        Story story = getById(storyId);
        story.setRating(newRating);
        story.setAmountOfMarks(newAmountOfMarks);
        storyDao.save(story);
    }

    @Override
    public Page<Story> getAllStories(Pageable pageable) {
        return storyDao.findAllApproved(pageable);
    }

    @Override
    public Page<Story> findFiltered(FilterDto filter, Pageable pageable) {
        String authorLogin = "%";
        String storyTitle = "%";
        if (filter.getAuthorLogin() != null) {
            authorLogin += filter.getAuthorLogin().toLowerCase() + "%";
        }
        if (filter.getStoryTitle() != null) {
            storyTitle += filter.getStoryTitle().toLowerCase() + "%";
        }

        List<Long> genresList = filter.getGenresId();
        if (genresList == null || genresList.isEmpty()) {
            genresList = genreService.getAllGenres().stream().map(Genre::getId).collect(Collectors.toList());
        }
        return storyDao.findFiltered(authorLogin, storyTitle, genresList, pageable);
    }


    @Override
    public Story getById(Long id) {
        Optional<Story> story = storyDao.findById(id);
        return story.orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public Story getByTitle(String title) {
        return storyDao.getFirstByTitle(title).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public List<Story> getBestStories() {
        return storyDao.getBestStories();
    }

    @Override
    public Page<Story> getStoriesByGenre(Long genreId, Pageable pageable) {
        return this.storyDao.getStoryIdByGenreId(genreId, pageable);
    }

    @Override
    public Optional<Story> getStoryToModerate() {
        return storyDao.getStoryToModerate();
    }

    @Override
    public void updateModeration(Story story, boolean onModeration) {
        story.setOnModeration(onModeration);
        storyDao.save(story);
    }

    @Override
    public void approveStory(Long storyId) {
        Story story = getById(storyId);
        story.setApproved(true);
        story.setOnModeration(false);
        story.setOnRefactoring(false);
        storyDao.save(story);
    }

    @Override
    public void rejectStory(Long storyId) {
        storyDao.delete(getById(storyId));
    }

    @Override
    public void setStoryOnRefactor(Long storyId) {
        Story story = getById(storyId);
        story.setOnModeration(false);
        story.setOnRefactoring(true);
        storyDao.save(story);
    }
}
