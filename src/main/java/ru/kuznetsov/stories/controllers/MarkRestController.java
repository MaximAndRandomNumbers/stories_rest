package ru.kuznetsov.stories.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kuznetsov.stories.dto.ChangedStoryParamsDto;
import ru.kuznetsov.stories.dto.MarkDto;
import ru.kuznetsov.stories.security.exceptions.AccessDeniedException;
import ru.kuznetsov.stories.security.exceptions.ValidationException;
import ru.kuznetsov.stories.services.data.interfaces.StoryService;
import ru.kuznetsov.stories.services.data.interfaces.UserStoryMarkService;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mark")
public class MarkRestController {

    private final UserStoryMarkService userStoryMarkService;

    @Autowired
    public MarkRestController(UserStoryMarkService userStoryMarkService) {
        this.userStoryMarkService = userStoryMarkService;
    }

    @GetMapping("/{storyId}")
    public ResponseEntity<?> getMark(@PathVariable Long storyId, Principal principal){
        Integer mark = userStoryMarkService.getMark(principal, storyId);
        Map<String, Integer> map = new HashMap<>();
        map.put("mark", mark);
        return ResponseEntity.ok(map);
    }

    @PostMapping
    public ResponseEntity<?> setMark(@RequestBody MarkDto markDto, Principal principal){

       try {
           userStoryMarkService.save(principal, markDto);
       } catch (ValidationException| AccessDeniedException ex) {
           return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
       }
        ChangedStoryParamsDto newParams =
                userStoryMarkService.changeStoryRating(markDto.getStoryId());
        return new ResponseEntity<>(newParams, HttpStatus.CREATED);
    }
}
