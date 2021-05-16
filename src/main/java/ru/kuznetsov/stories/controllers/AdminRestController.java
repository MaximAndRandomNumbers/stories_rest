package ru.kuznetsov.stories.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;
import ru.kuznetsov.stories.dto.GenreDto;
import ru.kuznetsov.stories.dto.UserDto;
import ru.kuznetsov.stories.models.Genre;
import ru.kuznetsov.stories.models.User;
import ru.kuznetsov.stories.security.exceptions.ValidationException;
import ru.kuznetsov.stories.services.data.interfaces.GenreService;
import ru.kuznetsov.stories.services.data.interfaces.StoryService;
import ru.kuznetsov.stories.services.data.interfaces.UserService;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final UserService userService;
    private final GenreService genreService;
    private final StoryService storyService;
    @Autowired
    public AdminRestController(UserService userService, GenreService genreService, StoryService storyService) {
        this.userService = userService;
        this.genreService = genreService;
        this.storyService = storyService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(Pageable pageable){
        try {
            return new ResponseEntity<>(userService.getUsers(pageable).map(UserDto::new), HttpStatus.OK);
        } catch (Exception ex){
            return new ResponseEntity<>("Нет пользоватлей", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users/{login}")
    public ResponseEntity<?> getUsersByLogin(@PathVariable("login") String login, Pageable pageable){
        try {
            return new ResponseEntity<>(userService.getUsersByLogin(login, pageable).map(UserDto::new), HttpStatus.OK);
        } catch (Exception ex){
            return new ResponseEntity<>("Нет пользоватлей", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users/autocomplete/{login}")
    public ResponseEntity<?> getUsersAutocomplete(@PathVariable("login") String login){
        try {
            return new ResponseEntity<>(userService.getUsersByLoginAutoComplete(login), HttpStatus.OK);
        } catch (Exception ex){
            return new ResponseEntity<>("Нет пользоватлей", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users/moderators")
    public ResponseEntity<?> getModerators(Pageable pageable){
        try {
            return ResponseEntity.ok(userService.getModerators(pageable).map(UserDto::new));
        } catch (Exception ex){
            return new ResponseEntity<>("Нет модераторов", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/genre")
    public ResponseEntity<?> addGenre(@RequestBody Map<String, String> genreName){
        try {
            Genre genre = genreService.addGenre(genreName.get("genreName"));
            return new ResponseEntity<>(new GenreDto(genre), HttpStatus.CREATED);
        } catch (ValidationException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id){
        try{
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException ex){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/stories/{id}")
    public ResponseEntity<?> deleteStory(@PathVariable("id") Long id){
        try{
            storyService.rejectStory(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/user/set/moderator")
    public ResponseEntity<?> setModeratorStatus(@RequestBody Map<String,Long> body){
        try{
            User user = userService.setModeratorRole(body.get("id"));
            return ResponseEntity.ok(new UserDto(user));
        } catch (NotFoundException ex){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/user/remove/moderator")
    public ResponseEntity<?> removeModeratorStatus(@RequestBody Map<String,Long> body){
        try{
            User user = userService.removeModeratorRole(body.get("id"));
            return ResponseEntity.ok(new UserDto(user));
        } catch (NotFoundException ex){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
