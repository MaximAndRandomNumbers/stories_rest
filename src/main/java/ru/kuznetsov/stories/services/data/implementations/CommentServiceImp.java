package ru.kuznetsov.stories.services.data.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ru.kuznetsov.stories.dao.CommentDao;
import ru.kuznetsov.stories.dto.CommentDto;
import ru.kuznetsov.stories.models.Comment;
import ru.kuznetsov.stories.models.Role;
import ru.kuznetsov.stories.models.User;
import ru.kuznetsov.stories.security.exceptions.AccessDeniedException;
import ru.kuznetsov.stories.security.exceptions.ValidationException;
import ru.kuznetsov.stories.services.data.interfaces.CommentService;
import ru.kuznetsov.stories.services.data.interfaces.StoryService;
import ru.kuznetsov.stories.services.data.interfaces.UserService;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;

@Service
public class CommentServiceImp implements CommentService {
    private final CommentDao commentDao;
    private final StoryService storyService;
    private final UserService userService;

    @Autowired
    public CommentServiceImp(CommentDao commentDao, StoryService storyService, UserService userService) {
        this.commentDao = commentDao;
        this.storyService = storyService;
        this.userService = userService;
    }
    @Override
    public void save(CommentDto commentDto, Principal principal) {
        Comment comment = new Comment();
        comment.setStory(storyService.getById(commentDto.getStoryId()));
        if(commentDto.getText() == null || commentDto.getText().equals("")){
            throw new ValidationException("Введите текст комментария!");
        }
        comment.setText(commentDto.getText());
        comment.setUser(userService.findByLogin(principal.getName()));
        comment.setDate(new Date());

        commentDao.save(comment);
    }

    @Override
    public Optional<Comment> getById(Long id) {
        return commentDao.findById(id);
    }

    @Override
    public void delete(Long id, Principal principal) {
        User user = userService.findByLogin(principal.getName());
        boolean isModerator = user.getRoles().stream().map(Role::getRoleName).anyMatch(name -> name.equals("ROLE_MODERATOR"));
        Comment comment = getById(id).orElseThrow(() -> new IllegalArgumentException("No such comment"));
        if(!comment.getUser().getLogin().equals(principal.getName()) && !isModerator){
            throw new AccessDeniedException("Can`t delete a comment of another user");
        }
        commentDao.delete(comment);
    }

    @Override
    public Page<Comment> getAllComments(Long storyId, Pageable pageable) {
        return commentDao.findByStoryId(storyId, pageable);
    }
}
