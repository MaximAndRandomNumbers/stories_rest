package ru.kuznetsov.stories.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kuznetsov.stories.dto.StoryCardDto;
import ru.kuznetsov.stories.dto.UserDto;
import ru.kuznetsov.stories.models.Role;
import ru.kuznetsov.stories.models.Story;
import ru.kuznetsov.stories.services.data.interfaces.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getRoles(Principal principal) {
        if(principal == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<String> roleNames = userService.findByLogin(principal.getName())
                .getRoles()
                .stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roleNames);
    }

    @GetMapping("/name")
    public ResponseEntity<String> getLogin(Principal principal){
        if(principal == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(principal.getName());
    }

    @GetMapping("/{login}/approved-stories")
    public ResponseEntity<?> getApprovedStories(@PathVariable String login){
        try{
            return ResponseEntity.ok(userService.findByLogin(login)
                    .getStories()
                    .stream()
                    .filter(Story::isApproved)
                    .map(StoryCardDto::new)
                    .collect(Collectors.toList()));
        } catch (NullPointerException ex) {
            return new ResponseEntity<>("Неизвестный пользователь", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{login}/refactor-stories")
    public ResponseEntity<?> getRefactorStories(@PathVariable String login, Principal principal){
        if(!login.equals(principal.getName())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try{
            return ResponseEntity.ok(userService.findByLogin(login)
                    .getStories()
                    .stream()
                    .filter(Story::isOnRefactoring)
                    .map(StoryCardDto::new)
                    .collect(Collectors.toList()));
        } catch (NullPointerException ex) {
            return new ResponseEntity<>("Неизвестный пользователь", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{login}/info")
    public ResponseEntity<?> getInfo(@PathVariable String login){
        try{
            return ResponseEntity.ok(new UserDto(userService.findByLogin(login)));
        } catch (NullPointerException ex){
            return new ResponseEntity<>("Неизвестный пользователь", HttpStatus.BAD_REQUEST);
        }
    }
}
