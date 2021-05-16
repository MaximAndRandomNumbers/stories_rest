package ru.kuznetsov.stories.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.kuznetsov.stories.dto.AuthenticationRequestDto;
import ru.kuznetsov.stories.dto.ChangePasswordDto;
import ru.kuznetsov.stories.dto.RefreshTokenDto;
import ru.kuznetsov.stories.dto.RegRequestDto;
import ru.kuznetsov.stories.security.captcha.CaptchaService;
import ru.kuznetsov.stories.security.exceptions.RefreshTokenException;
import ru.kuznetsov.stories.security.exceptions.ValidationException;
import ru.kuznetsov.stories.security.jwt.RefreshTokenService;
import ru.kuznetsov.stories.services.register_and_auth.AuthService;
import ru.kuznetsov.stories.services.register_and_auth.VerificationException;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final CaptchaService captchaService;

    @Autowired
    public AuthRestController(AuthService authService, RefreshTokenService refreshTokenService, CaptchaService captchaService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.captchaService = captchaService;
    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequestDto requestDto) {
        try{
            return ResponseEntity.ok(authService.login(requestDto));
        } catch (BadCredentialsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody @Valid RefreshTokenDto requestDto) {
        try{
            return ResponseEntity.ok(authService.refresh(requestDto));
        } catch (RefreshTokenException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public void logout(@RequestBody @Valid RefreshTokenDto requestDto){
        refreshTokenService.deleteByToken(requestDto.getRefreshToken());
    }

    @PostMapping("/reg")
    public ResponseEntity<?> register(@RequestBody @Valid RegRequestDto regDto,Errors errors,
                                      @RequestParam("g-recaptcha-response") String captchaResponse){
        if(errors.hasErrors()){
            return new ResponseEntity<>(errors.getFieldError().getDefaultMessage(),HttpStatus.BAD_REQUEST);
        }
        try {
            captchaService.verifyCaptcha(captchaResponse);
            authService.register(regDto);
        } catch (ValidationException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Пользователь был зарегистрирован." +
                " Пожалуйста проверьте свою почту для активации аккаунта", HttpStatus.CREATED);
    }


    @GetMapping("/verification/{token}")
    public ResponseEntity<?> verify(@PathVariable String token) {
        try {
            authService.verifyUser(token);
            return new ResponseEntity<>("Ваш аккаунт был успешно активирован." +
                    " Наслаждайтесь приложением!", HttpStatus.OK);
        } catch (VerificationException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/forget")
    public ResponseEntity<?> forgetPassword(@RequestBody Map<String, String> requestBody){
        try {
            authService.forgetPassword(requestBody.get("email"));
            return ResponseEntity.ok("Инструкции для смены пароля высланы на указанную почту");
        } catch(ValidationException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/new_password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordDto passwordDto, Errors errors){
        if(errors.hasErrors()){
            return new ResponseEntity<>(errors.getFieldError().getDefaultMessage(),HttpStatus.BAD_REQUEST);
        }
        try{
            authService.changePassword(passwordDto);
            return ResponseEntity.ok("Пароль успешно изменён!");
        } catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
