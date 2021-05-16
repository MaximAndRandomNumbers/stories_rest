package ru.kuznetsov.stories.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kuznetsov.stories.dao.RefreshTokenDao;
import ru.kuznetsov.stories.models.RefreshToken;
import ru.kuznetsov.stories.models.User;
import ru.kuznetsov.stories.security.exceptions.RefreshTokenException;

import java.util.Date;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenDao refreshTokenDao;

    @Autowired
    public RefreshTokenService(RefreshTokenDao refreshTokenDao) {
        this.refreshTokenDao = refreshTokenDao;
    }

    public String generateToken(User user){
        RefreshToken refreshToken = refreshTokenDao.getByUser(user).orElse(new RefreshToken());
        String token = UUID.randomUUID().toString();
        refreshToken.setToken(token);
        refreshToken.setCreatedDate(new Date());
        refreshToken.setUser(user);

        refreshTokenDao.save(refreshToken);
        return token;
    }

    public void deleteByToken(String token){
        refreshTokenDao.findByToken(token).ifPresent(refreshTokenDao::delete);
    }
    public void validateRefreshToken(User user, String providedToken){
        RefreshToken refreshToken = refreshTokenDao.getByUser(user)
                .orElseThrow(() -> new RefreshTokenException("Invalid refresh token"));

        if(!refreshToken.getToken().equals(providedToken)) {
            throw new RefreshTokenException("Invalid refresh token");
        }
    }

}
