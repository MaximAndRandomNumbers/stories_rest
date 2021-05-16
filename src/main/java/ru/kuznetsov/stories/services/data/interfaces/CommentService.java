package ru.kuznetsov.stories.services.data.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.kuznetsov.stories.dto.CommentDto;
import ru.kuznetsov.stories.models.Comment;

import java.security.Principal;
import java.util.Optional;

@Component
public interface CommentService {
    void save(CommentDto comment, Principal principal);
    Optional<Comment> getById(Long id);
    void delete(Long id, Principal principal);
    Page<Comment> getAllComments(Long storyId, Pageable pageable);
}
