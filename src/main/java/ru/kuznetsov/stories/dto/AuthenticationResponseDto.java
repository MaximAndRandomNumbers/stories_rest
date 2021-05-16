package ru.kuznetsov.stories.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationResponseDto {
    private String jwtToken;
    private String refreshToken;
    private String login;
}
