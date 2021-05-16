package ru.kuznetsov.stories.services.data.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import ru.kuznetsov.stories.dto.FilterDto;
import ru.kuznetsov.stories.dto.StoryDto;
import ru.kuznetsov.stories.models.Story;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Component
public interface StoryService {
    void save(StoryDto story, Principal principal);
    void updateStory(StoryDto story, Principal principal);
    void updateRating(Long storyId, Double newRating, Long newAmountOfMarks);
    Page<Story> getAllStories(Pageable pageable);
    Page<Story> findFiltered(FilterDto filter, Pageable pageable);
    Story getById(Long id);
    Story getByTitle(String title);
    List<Story> getBestStories();
    Page<Story> getStoriesByGenre(Long genreId, Pageable pageable);
    Optional<Story> getStoryToModerate();
    void updateModeration(Story story, boolean onModeration);
    void approveStory(Long storyId);
    void rejectStory(Long storyId);
    void setStoryOnRefactor(Long storyId);
    void deleteStory(Long storyId, Principal principal);
    List<String> findStoriesByPartOfTitle(String partOfTitle);
    List<String> findStoriesByPartOfAuthorLogin(String partOfAuthorLogin);

}
