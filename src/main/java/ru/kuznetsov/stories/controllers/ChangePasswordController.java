package ru.kuznetsov.stories.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.kuznetsov.stories.security.exceptions.ValidationException;
import ru.kuznetsov.stories.services.register_and_auth.AuthService;
import ru.kuznetsov.stories.services.register_and_auth.VerificationException;

@Controller
public class ChangePasswordController {

    private final AuthService authService;

    @Autowired
    public ChangePasswordController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/change_password/{token}")
    public String changePassword(@PathVariable String token){
        try{
            this.authService.checkChangePasswordToken(token);
        } catch (VerificationException ex){
            return "forward:/not_found.html";
        }
        return "forward:/index.html";
    }
}
