package ru.kuznetsov.stories.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kuznetsov.stories.dto.CommentDto;
import ru.kuznetsov.stories.security.exceptions.AccessDeniedException;
import ru.kuznetsov.stories.security.exceptions.ValidationException;
import ru.kuznetsov.stories.services.data.interfaces.CommentService;

import java.security.Principal;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {

    private final CommentService commentService;

    @Autowired
    public CommentRestController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/story/{id}")
    public ResponseEntity<Page<CommentDto>> getAllComments(@PathVariable Long id, Pageable pageable){
        Page<CommentDto> comments = commentService.getAllComments(id,pageable).map(CommentDto::new);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<?> postComment(@RequestBody CommentDto commentDto, Principal principal){
        try {
            commentService.save(commentDto, principal);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(ValidationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id, Principal principal){
        try {
            commentService.delete(id, principal);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

}
