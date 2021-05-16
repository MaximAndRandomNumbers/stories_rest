package ru.kuznetsov.stories.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenDto {

    @NotBlank
    private String refreshToken;
    private String login;
}
