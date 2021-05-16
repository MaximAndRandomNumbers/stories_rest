package ru.kuznetsov.stories.security.jwt;

import org.springframework.stereotype.Component;
import ru.kuznetsov.stories.models.User;

@Component
public class JwtUserFactory {

    public JwtUserFactory() {}

    public static JwtUser create(User user){
        return new JwtUser(
                user.getLogin(),
                user.getPassword(),
                user.getRoles(),
                user.getEnabled()
        );
    }
}
