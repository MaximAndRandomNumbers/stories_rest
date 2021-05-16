package ru.kuznetsov.stories.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDto {
    String token;

    @NotBlank(message = "Введите пароль")
    @Length(min=8, message = "Слишком короткий пароль")
    String newPassword;
}
