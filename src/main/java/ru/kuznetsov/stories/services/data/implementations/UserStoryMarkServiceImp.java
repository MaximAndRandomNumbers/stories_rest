package ru.kuznetsov.stories.services.data.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kuznetsov.stories.dao.UserStoryMarkDao;
import ru.kuznetsov.stories.dto.ChangedStoryParamsDto;
import ru.kuznetsov.stories.dto.MarkDto;
import ru.kuznetsov.stories.models.Story;
import ru.kuznetsov.stories.models.User;
import ru.kuznetsov.stories.models.UserStoryMark;
import ru.kuznetsov.stories.models.UserStoryMarkPK;
import ru.kuznetsov.stories.security.exceptions.AccessDeniedException;
import ru.kuznetsov.stories.security.exceptions.ValidationException;
import ru.kuznetsov.stories.services.data.interfaces.StoryService;
import ru.kuznetsov.stories.services.data.interfaces.UserService;
import ru.kuznetsov.stories.services.data.interfaces.UserStoryMarkService;

import java.security.Principal;
import java.util.Optional;


@Service
public class UserStoryMarkServiceImp implements UserStoryMarkService {
    private final UserStoryMarkDao userStoryMarkDao;
    private final UserService userService;
    private final StoryService storyService;

    @Autowired
    public UserStoryMarkServiceImp(UserStoryMarkDao userStoryMarkDao, UserService userService, StoryService storyService) {
        this.userStoryMarkDao = userStoryMarkDao;
        this.userService = userService;
        this.storyService = storyService;
    }

    @Override
    public void save(Principal principal, MarkDto markDto) {
        validateMark(markDto);
        User user = userService.findByLogin(principal.getName());
        Story story = storyService.getById(markDto.getStoryId());
        if(user.getLogin().equals(story.getAuthor().getLogin())){
            throw new AccessDeniedException("Нельзя оценить свой рассказ");
        }
        userStoryMarkDao.save(new UserStoryMark(user, story, markDto.getMark()));
    }

    private void validateMark(MarkDto markDto){
        Integer mark = markDto.getMark();
        if(mark == null || mark < 1 || mark > 5) {
            throw new ValidationException("Неверная оценка!");
        }
    }

    @Override
    public Integer getMark(Principal principal, Long storyId) {
        User user = userService.findByLogin(principal.getName());
        UserStoryMarkPK id = new UserStoryMarkPK(user.getId(), storyId);
        Optional<UserStoryMark> userStoryMark = userStoryMarkDao.findById(id);
        return userStoryMark.map(UserStoryMark::getMark).orElse(null);
    }

    @Override
    public Double findSumByStoryId(Long storyId) {
        return userStoryMarkDao.findSumByStoryId(storyId);
    }

    @Override
    public Long findAmountOfMarksByStoryId(Long storyId) {
        return userStoryMarkDao.findAmountOfMarksByStoryId(storyId);
    }

    @Override
    public ChangedStoryParamsDto changeStoryRating(Long storyId) {
        Double sum = findSumByStoryId(storyId);
        Long amount = findAmountOfMarksByStoryId(storyId);
        Double newRating = sum/amount;
        storyService.updateRating(storyId, newRating, amount);
        return new ChangedStoryParamsDto(newRating, amount);
    }


}
