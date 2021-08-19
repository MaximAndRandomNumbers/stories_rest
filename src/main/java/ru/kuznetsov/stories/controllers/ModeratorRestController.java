package ru.kuznetsov.stories.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kuznetsov.stories.dto.StoryDto;
import ru.kuznetsov.stories.security.exceptions.AccessDeniedException;
import ru.kuznetsov.stories.services.data.interfaces.ModerationService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/moderator")
public class ModeratorRestController {

    private final ModerationService moderationService;

    @Autowired
    public ModeratorRestController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @GetMapping("/get/story")
    public ResponseEntity<?> getStoryToModerate(Principal principal) {
        StoryDto storyDto = moderationService.getStoryToModerate(principal);
        if (storyDto == null) {
            return new ResponseEntity<>("No stories available", HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(storyDto);
        }
    }

    @GetMapping("/approve/{storyId}")
    public ResponseEntity<?> approveStory(@PathVariable Long storyId, Principal principal) {
        try {
            moderationService.approveStory(storyId, principal);
            return ResponseEntity.ok("Рассказ был одобрен");
        } catch (AccessDeniedException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Что-то пошло не так", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/refactor/{storyId}")
    public ResponseEntity<?> refactor(@PathVariable Long storyId, @RequestBody Map<String, String> comment, Principal principal) {
        try {
            moderationService.returnStoryToRefactor(storyId, comment.get("comment"), principal);
            return ResponseEntity.ok("Рассказ был отправлен на доработку");
        } catch (AccessDeniedException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Что-то пошло не так", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/reject/{storyId}")
    public ResponseEntity<?> reject(@PathVariable Long storyId, Principal principal) {
        try {
            moderationService.rejectStory(storyId, principal);
            return ResponseEntity.ok("Рассказ удалён");
        } catch (AccessDeniedException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Что-то пошло не так", HttpStatus.BAD_REQUEST);
        }
    }
}
