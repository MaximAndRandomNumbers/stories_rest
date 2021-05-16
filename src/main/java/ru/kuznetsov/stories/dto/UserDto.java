package ru.kuznetsov.stories.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kuznetsov.stories.models.Role;
import ru.kuznetsov.stories.models.User;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String login;
    private String email;
    private Date registrationDate;
    private List<String> roles;

    public UserDto(User user){
        id = user.getId();
        login = user.getLogin();
        registrationDate = user.getRegistrationDate();
        roles = user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList());
        email = user.getEmail();
    }
}
