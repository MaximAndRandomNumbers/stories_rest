package ru.kuznetsov.stories.services.register_and_auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kuznetsov.stories.dto.*;
import ru.kuznetsov.stories.models.User;
import ru.kuznetsov.stories.models.VerificationToken;
import ru.kuznetsov.stories.security.exceptions.RefreshTokenException;
import ru.kuznetsov.stories.security.exceptions.ValidationException;
import ru.kuznetsov.stories.security.jwt.JwtTokenProvider;
import ru.kuznetsov.stories.security.jwt.RefreshTokenService;
import ru.kuznetsov.stories.services.data.interfaces.UserService;

@Service
public class AuthService {

    @Value("${configuration.host}")
    private String HOST;
    @Value("${configuration.protocol}")
    private String PROTOCOL;


    private final UserService userService;
    private final VerificationTokenService verificationTokenService;
    private final MailSender mailSender;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthService(UserService userService, VerificationTokenService verificationTokenService,
                       MailSender mailSender, AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
        this.mailSender = mailSender;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    public void register(RegRequestDto regRequest) {
        validateRequest(regRequest);
        User user = userService.save(regRequest);
        VerificationToken token = verificationTokenService.generateVerificationToken(user);
        mailSender.send(regRequest.getEmail(), "Подтверждение аккаунта",
                "Чтобы активировать аккаунт" +
                " перейдите по ссылке:" +
                " <a href='"+PROTOCOL +"://"+HOST +"/api/auth/verification/" + token.getToken()+"'>активировать</a>");
    }

    private void validateRequest(RegRequestDto regRequestDto) {
        if (userService.findByLogin(regRequestDto.getLogin()) != null) {
            throw new ValidationException("Данный логин уже занят");
        }
        if (userService.findByEmail(regRequestDto.getEmail()) != null) {
            throw new ValidationException("Данный email уже занят");
        }
    }

    public void verifyUser(String token) {
        verificationTokenService.verifyUserByToken(token);
    }

    public AuthenticationResponseDto login(AuthenticationRequestDto requestDto) {

        String login = requestDto.getLogin();
        String password = requestDto.getPassword();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        User user = userService.findByLogin(login);
        if(user == null){
            user = userService.findByEmail(login);
        }
        String jwtToken = jwtTokenProvider.createToken(user.getLogin());
        String refreshToken = refreshTokenService.generateToken(user);
        return AuthenticationResponseDto.builder()
                .jwtToken(jwtToken)
                .refreshToken(refreshToken)
                .login(user.getLogin())
                .build();

    }

    public void forgetPassword(String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new ValidationException("Нет пользователя с данным email");
        }
        VerificationToken token = verificationTokenService.generateVerificationToken(user);
        mailSender.send(email, "Смена пароля",
                "Чтобы поменять пароль" +
                        " <a href='"+PROTOCOL +"://"+HOST +"/change_password/"
                        + token.getToken() + "'>ссылка</a>");

    }
    public void checkChangePasswordToken(String token){
        verificationTokenService.validateToken(token);
    }
    public void changePassword(ChangePasswordDto passwordDto){
        checkChangePasswordToken(passwordDto.getToken());
        User user = verificationTokenService.getUserByToken(passwordDto.getToken());
        userService.updatePassword(user, passwordDto.getNewPassword());
        verificationTokenService.deleteToken(passwordDto.getToken());
    }
    public AuthenticationResponseDto refresh(RefreshTokenDto refreshTokenDto) {
        String login = refreshTokenDto.getLogin();
        String refreshToken = refreshTokenDto.getRefreshToken();
        User user = userService.findByLogin(login);
        if (user == null) {
            throw new RefreshTokenException("Пользователь с данным логином не найден");
        }
        refreshTokenService.validateRefreshToken(user, refreshToken);
        String jwtToken = jwtTokenProvider.createToken(login);
        String newRefreshToken = refreshTokenService.generateToken(user);

        return AuthenticationResponseDto.builder()
                .jwtToken(jwtToken)
                .refreshToken(newRefreshToken)
                .login(login)
                .build();
    }
}
