package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.BaseValidation;
import ru.yandex.practicum.filmorate.validation.PartialValidation;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class User {
    private int id;
    @Email(groups = {BaseValidation.class, PartialValidation.class}, message = "Некорретный формат email")
    @NotBlank(groups = BaseValidation.class, message = "Email не может быть пустым")
    private String email;
    @NotBlank(groups = {BaseValidation.class, PartialValidation.class}, message = "login не может быть пустым")
    private String login;
    private String name;
    @PastOrPresent(groups = {BaseValidation.class, PartialValidation.class},
            message = "День рождения не может быть в будущем")
    @NotNull(groups = {BaseValidation.class, PartialValidation.class},
            message = "День рождения должен быть обязательно указан")
    private LocalDate birthday;
}
