package ru.kuznetsov.stories.services.data.interfaces;

import org.springframework.stereotype.Component;
import ru.kuznetsov.stories.dto.StoryDto;

import java.security.Principal;

@Component
public interface ModerationService {
    StoryDto getStoryToModerate(Principal principal);
    void approveStory(Long storyId, Principal principal);
    void rejectStory(Long storyId, Principal principal);
    void returnStoryToRefactor(Long storyId, String comment, Principal principal);
}
