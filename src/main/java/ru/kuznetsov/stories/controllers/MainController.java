package ru.kuznetsov.stories.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {

    @GetMapping({"/", "/sign-in", "/sign-up", "/stories", "/story/**",
            "/refactor/**", "/notifications", "/notification/**","/moderator",
    "/publish","/user/**", "/forget",  "/admin"})
    public String home() {
        return "forward:/index.html";
    }

}
