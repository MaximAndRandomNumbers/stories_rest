package ru.kuznetsov.stories.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kuznetsov.stories.models.User;
import ru.kuznetsov.stories.security.exceptions.AccessDeniedException;
import ru.kuznetsov.stories.services.data.interfaces.NotificationService;
import ru.kuznetsov.stories.services.data.interfaces.UserService;

import java.security.Principal;

@RestController
@RequestMapping("/api/notifications")
public class NotificationRestController {

    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public NotificationRestController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getNotifications(Principal principal){
        User user = userService.findByLogin(principal.getName());
        return ResponseEntity.ok(notificationService.getNotifications(user));
    }

    @GetMapping("/amount")
    public ResponseEntity<Long> getNotificationsAmount(Principal principal){
        User user = userService.findByLogin(principal.getName());
        return ResponseEntity.ok(notificationService.getAmountOfNewNotifications(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNotification(@PathVariable Long id, Principal principal){
        User user = userService.findByLogin(principal.getName());
        try {
            return ResponseEntity.ok(notificationService.getNotification(user, id));
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>("Invalid id",HttpStatus.BAD_REQUEST);
        } catch (AccessDeniedException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}
