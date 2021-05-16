package ru.kuznetsov.stories.services.register_and_auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kuznetsov.stories.dao.VerificationTokenDao;
import ru.kuznetsov.stories.models.User;
import ru.kuznetsov.stories.models.VerificationToken;
import ru.kuznetsov.stories.services.data.interfaces.UserService;

import java.util.Date;
import java.util.UUID;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

    @Value("${email_token.time}")
    private Long verificationTokenTimeInMilliseconds;
    private final VerificationTokenDao verificationTokenDao;
    private final UserService userService;

    @Autowired
    public VerificationTokenServiceImpl(VerificationTokenDao verificationTokenDao, UserService userService) {
        this.verificationTokenDao = verificationTokenDao;
        this.userService = userService;
    }

    @Override
    @Transactional
    public VerificationToken generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(
                new Date(new Date().getTime() + verificationTokenTimeInMilliseconds));
        verificationTokenDao.save(verificationToken);
        return  verificationToken;
    }

    @Override
    public void verifyUserByToken(String token) {
        try{
            VerificationToken verificationToken = verificationTokenDao.findByToken(token);
            if(verificationToken.getExpiryDate().before(new Date())){
                throw new VerificationException("");
            }
            userService.enableUser(verificationToken.getUser());
            verificationTokenDao.delete(verificationToken);
        } catch (Exception e) {
            throw new VerificationException("Incorrect or expired verification token");
        }
    }

    @Override
    public void validateToken(String token){
        try{
            VerificationToken verificationToken = verificationTokenDao.findByToken(token);
            if(verificationToken.getExpiryDate().before(new Date())){
                throw new VerificationException("");
            }
        } catch (Exception ex){
            throw new VerificationException("Incorrect or expired verification token");
        }
    }

    @Override
    public User getUserByToken(String token) {
        try {
            VerificationToken verificationToken = verificationTokenDao.findByToken(token);
            return verificationToken.getUser();
        } catch (NullPointerException ex){
            return null;
        }
    }
}
