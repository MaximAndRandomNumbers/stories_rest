package ru.kuznetsov.stories.services.data.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kuznetsov.stories.dao.ModeratedStoryDao;
import ru.kuznetsov.stories.models.ModeratedStory;
import ru.kuznetsov.stories.models.Story;
import ru.kuznetsov.stories.models.User;
import ru.kuznetsov.stories.services.data.interfaces.ModeratedStoryService;
import ru.kuznetsov.stories.services.data.interfaces.StoryService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ModeratedStoryServiceImp implements ModeratedStoryService {

    private final ModeratedStoryDao moderatedStoryDao;
    private final StoryService storyService;

    @Autowired
    public ModeratedStoryServiceImp(ModeratedStoryDao moderatedStoryDao, StoryService storyService) {
        this.moderatedStoryDao = moderatedStoryDao;
        this.storyService = storyService;
    }

    @Override
    @Scheduled(fixedDelay = 24*60*60*1000)
    public void sendExpiredStoriesOnModeration() {
        List<ModeratedStory> expiredStories = moderatedStoryDao.getExpiredStories(new Date());
        for(ModeratedStory story: expiredStories){
            storyService.updateModeration(story.getStory(), false);
            moderatedStoryDao.delete(story);
        }
    }

    @Override
    public Story getStoryToModeration(User moderator) {
        return moderatedStoryDao.findByModeratorId(moderator.getId())
                .map(ModeratedStory::getStory).orElse(null);
    }

    @Override
    public void addStory(Story story, User moderator) {
        moderatedStoryDao.modifyingQueryInsertPerson(story.getId(), moderator.getId(), new Date());
    }

    @Override
    public void deleteStory(Story story) {
        moderatedStoryDao.delete(moderatedStoryDao.findByStoryId(story.getId()));
    }


}
