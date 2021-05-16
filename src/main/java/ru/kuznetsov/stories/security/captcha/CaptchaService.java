package ru.kuznetsov.stories.security.captcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.kuznetsov.stories.dto.CaptchaResponseDto;
import ru.kuznetsov.stories.security.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class CaptchaService {

    private final RestTemplate restTemplate;

    private final static String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";
    @Value("${recaptcha.secret}")
    private String captchaSecret;


    @Autowired
    public CaptchaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void verifyCaptcha(String captchaResponse){
        String url = String.format(CAPTCHA_URL, captchaSecret, captchaResponse);
        CaptchaResponseDto responseDto = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);
        if(responseDto == null || !responseDto.isSuccess()){
            throw new ValidationException("Подтвердите, что вы не робот");
        }
    }
}
