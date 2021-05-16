package ru.kuznetsov.stories.services.data.interfaces;

import org.springframework.stereotype.Component;
import ru.kuznetsov.stories.dto.ChangedStoryParamsDto;
import ru.kuznetsov.stories.dto.MarkDto;
import ru.kuznetsov.stories.models.Story;
import ru.kuznetsov.stories.models.User;

import java.security.Principal;

@Component
public interface UserStoryMarkService {
    void save(Principal principal, MarkDto markDto);
    Integer getMark(Principal principal, Long storyId);
    Double findSumByStoryId(Long storyId);
    Long findAmountOfMarksByStoryId(Long storyId);
    ChangedStoryParamsDto changeStoryRating(Long storyId);

}
