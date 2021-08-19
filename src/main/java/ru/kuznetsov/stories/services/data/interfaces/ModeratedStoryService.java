package ru.kuznetsov.stories.services.data.interfaces;

import org.springframework.stereotype.Component;
import ru.kuznetsov.stories.models.Story;
import ru.kuznetsov.stories.models.User;

import java.security.Principal;

@Component
public interface ModeratedStoryService {

    void sendExpiredStoriesOnModeration();
    Story getStoryToModeration(User moderator);
    void addStory(Story story, User moderator);
    void deleteStory(Story story);
    boolean checkStory(Principal principal, Story story);
}
