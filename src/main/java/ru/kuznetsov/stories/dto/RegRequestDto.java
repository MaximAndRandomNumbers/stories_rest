package ru.kuznetsov.stories.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegRequestDto {

    @NotBlank(message = "Введите логин")
    @Length(min = 5, message = "Слишком короткий логин")
    private String login;

    @NotBlank(message = "Введите пароль")
    @Length(min=8, message = "Слишком короткий пароль")
    private String password;

    @NotBlank(message = "Заполните email")
    @Email(message = "Неверный формат email")
    private String email;
}

