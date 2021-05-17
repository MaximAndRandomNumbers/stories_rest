package ru.kuznetsov.stories.services.register_and_auth;

import org.springframework.stereotype.Component;
import ru.kuznetsov.stories.models.User;
import ru.kuznetsov.stories.models.VerificationToken;

@Component
public interface VerificationTokenService {
    VerificationToken generateVerificationToken(User user);
    void verifyUserByToken(String token);
    void validateToken(String token);
    User getUserByToken(String token);
    void deleteToken(String token);
}
