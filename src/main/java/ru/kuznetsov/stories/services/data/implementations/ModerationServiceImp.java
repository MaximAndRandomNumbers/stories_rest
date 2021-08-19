package ru.kuznetsov.stories.services.data.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.kuznetsov.stories.dto.StoryDto;
import ru.kuznetsov.stories.models.Story;
import ru.kuznetsov.stories.models.User;
import ru.kuznetsov.stories.security.exceptions.AccessDeniedException;
import ru.kuznetsov.stories.services.data.interfaces.*;

import java.security.Principal;
import java.util.Optional;

@Service
public class ModerationServiceImp implements ModerationService {

    private final StoryService storyService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ModeratedStoryService moderatedStoryService;

    @Value("${notification.moderator.theme}")
    private String MODERATION_THEME;
    @Value("${notification.approved.text}")
    private String APPROVED_TEXT;
    @Value("${notification.refactor.text}")
    private String REFACTOR_TEXT;
    @Value("${notification.reject.text}")
    private String REJECT_TEXT;
    @Value("${notification.story.url}")
    private String STORY_URL;
    @Value("${notification.story.refactor.url}")
    private String REFACTOR_URL;

    @Autowired
    public ModerationServiceImp(StoryService storyService, UserService userService,
                                NotificationService notificationService,
                                SimpMessagingTemplate simpMessagingTemplate,
                                ModeratedStoryService moderatedStoryService) {
        this.storyService = storyService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.moderatedStoryService = moderatedStoryService;
    }

    @Override
    public StoryDto getStoryToModerate(Principal principal) {

        User user = userService.findByLogin(principal.getName());

        //If moderator has already taken a story to moderate then return him the story
        Story story = moderatedStoryService.getStoryToModeration(user);
        if(story != null){
            System.out.println(story.getTitle());
            return new StoryDto(story);
        }

        //Trying to find stories which are not moderated, not approved and not on refactoring
        Optional<Story> optionalStory = storyService.getStoryToModerate();
        if(!optionalStory.isPresent()){
            return null;
        }
        story = optionalStory.get();
        moderatedStoryService.addStory(story, user);
        storyService.updateModeration(story, true);
        return new StoryDto(story);
    }

    private void isStoryAvailableForModeration(Story story, Principal principal){
        if(!moderatedStoryService.checkStory(principal, story)){
            throw new AccessDeniedException("Вы не модерируете данный рассказ");
        }
    }
    @Override
    public void approveStory(Long storyId, Principal principal) {
        Story story = storyService.getById(storyId);
        isStoryAvailableForModeration(story, principal);
        storyService.approveStory(storyId);
        moderatedStoryService.deleteStory(story);
        //Adding notification to db
        notificationService.sendNotification(
                story.getAuthor(),MODERATION_THEME,
                APPROVED_TEXT + "\n<a>" + STORY_URL + storyId+"</a>");

        //Sending notification to the user via STOMP sub-protocol
        simpMessagingTemplate.convertAndSendToUser(
                story.getAuthor().getLogin(),"/queue/notification",APPROVED_TEXT);
    }

    @Override
    public void rejectStory(Long storyId, Principal principal) {
        Story story = storyService.getById(storyId);
        isStoryAvailableForModeration(story, principal);
        storyService.rejectStory(storyId);
        //Adding notification to db
        notificationService.sendNotification(
                story.getAuthor(),MODERATION_THEME,
                REJECT_TEXT + "\nРассказ: "+ story.getTitle());

        //Sending notification to the user via STOMP sub-protocol
        simpMessagingTemplate.convertAndSendToUser(
                story.getAuthor().getLogin(),"/queue/notification",REJECT_TEXT);
    }

    @Override
    public void returnStoryToRefactor(Long storyId, String comment, Principal principal) {
        Story story = storyService.getById(storyId);
        isStoryAvailableForModeration(story, principal);
        storyService.setStoryOnRefactor(storyId);
        moderatedStoryService.deleteStory(story);
        //Adding notification to db
        notificationService.sendNotification(
                story.getAuthor(),MODERATION_THEME,
                REFACTOR_TEXT + "\n<a>" +REFACTOR_URL + storyId + "</a>\nКомментарий модератора:\n" + comment );

        //Sending notification to the user via STOMP sub-protocol
        simpMessagingTemplate.convertAndSendToUser(
                story.getAuthor().getLogin(),"/queue/notification",REFACTOR_TEXT);
    }
}
