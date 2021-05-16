package ru.kuznetsov.stories.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kuznetsov.stories.dto.FilterDto;
import ru.kuznetsov.stories.dto.StoryCardDto;
import ru.kuznetsov.stories.dto.StoryDto;
import ru.kuznetsov.stories.models.Story;
import ru.kuznetsov.stories.security.exceptions.ValidationException;
import ru.kuznetsov.stories.services.data.interfaces.StoryService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/stories")
public class StoryRestController {

    private final StoryService storyService;

    @Autowired
    public StoryRestController(StoryService storyService) {
        this.storyService = storyService;
    }

    @GetMapping
    public ResponseEntity<Page<StoryCardDto>> getStories(
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable){
        Page<StoryCardDto> storiesDto = storyService.getAllStories(pageable).map(StoryCardDto::new);
        return ResponseEntity.ok(storiesDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryDto> getStory(@PathVariable Long id){
        try{
            StoryDto storyDto = new StoryDto(storyService.getById(id));
            return ResponseEntity.ok(storyDto);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<Page<StoryCardDto>> getStoriesByGenre(@PathVariable Long genreId, Pageable pageable){
        Page<StoryCardDto> stories = storyService.getStoriesByGenre(genreId, pageable).map(StoryCardDto::new);

        return ResponseEntity.ok(stories);
    }

    @PostMapping("/filtered")
    public ResponseEntity<Page<StoryCardDto>> getFiltered(@RequestBody FilterDto filter, Pageable pageable){
        Page<StoryCardDto> stories = storyService.findFiltered(filter, pageable).map(StoryCardDto::new);

        return ResponseEntity.ok(stories);
    }

    @GetMapping("/best")
    public ResponseEntity<List<StoryCardDto>> getBestStories(){
        List<StoryCardDto> bestStories = storyService.getBestStories()
                .stream().map(StoryCardDto::new).collect(Collectors.toList());

        return ResponseEntity.ok(bestStories);
    }

    @PostMapping
    public ResponseEntity<?> publishStory(@RequestBody StoryDto storyDto, Principal principal){
        try {
            storyService.save(storyDto, principal);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (ValidationException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);}
//        } catch (RuntimeException e){
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
    }
    @PostMapping("/update/{id}")
    public ResponseEntity<?> refactorStory(@PathVariable Long id, @RequestBody StoryDto storyDto, Principal principal){
        try {
            storyDto.setId(id);
            storyService.updateStory(storyDto, principal);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (ValidationException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NullPointerException e){
            return new ResponseEntity<>("Данного рассказа не существует", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStory(@PathVariable Long id, Principal principal){
        try{
            storyService.deleteStory(id, principal);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ValidationException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (NullPointerException e){
            return new ResponseEntity<>("Данного рассказа не существует", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/autocomplete/title/{title}")
    public ResponseEntity<List<String>> getStoriesOptionsByTitle(@PathVariable String title){
        return ResponseEntity.ok(storyService.findStoriesByPartOfTitle(title));
    }

    @GetMapping("/autocomplete/author/{login}")
    public ResponseEntity<List<String>> getStoriesOptionsByAuthorLogin(@PathVariable String login){
        return ResponseEntity.ok(storyService.findStoriesByPartOfAuthorLogin(login));
    }
}
