package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.LaterDate;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class Film {
    private int id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    @Size(max = 200, message = "Размер описания не должен превышать 200 символов")
    private String description;
    @LaterDate(value = "1895-12-28",
            message = "Дата релиза не может быть раньше чем день рождения кино 28-12-1895")
    // "1895-12-28" it's the movie's birthday
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
    private Set<Integer> likes;
}
