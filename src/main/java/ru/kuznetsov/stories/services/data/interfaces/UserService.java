package ru.kuznetsov.stories.services.data.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import ru.kuznetsov.stories.dto.RegRequestDto;
import ru.kuznetsov.stories.models.User;

import java.util.List;
import java.util.Optional;


@Component
public interface UserService{
    User save(RegRequestDto regRequestDto);
    User findByLogin(String login);
    User findByEmail(String email);
    void enableUser(User user);
    void updatePassword(User user, String newPassword);
    void deleteUser(Long id);
    Page<User> getUsers(Pageable pageable);
    Page<User> getUsersByLogin(String loginPart, Pageable pageable);
    List<String> getUsersByLoginAutoComplete(String loginPart);
    User getById(Long id);
    Page<User> getModerators(Pageable pageable);
    User setModeratorRole(Long id);
    User removeModeratorRole(Long id);
}
