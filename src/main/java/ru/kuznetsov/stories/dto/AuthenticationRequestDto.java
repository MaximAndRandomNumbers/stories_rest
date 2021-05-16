package ru.kuznetsov.stories.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequestDto {
    private String login;
    private String password;
}
