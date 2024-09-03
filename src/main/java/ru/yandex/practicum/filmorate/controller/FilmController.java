package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int currentId = 1;
    private static final LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);

    @GetMapping
    public Collection<Film> getAll() {
        log.info("Поступил Get запрос на получение списка всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        log.info("Поступил POST запрос с телом: {}", film);
        if (film.getReleaseDate().isBefore(MOVIE_BIRTHDAY)) {
            log.warn("Некорректная дата релиза"); //Не стал  создавать свой валидатор даты. Мне кажется это лишним.
            throw new ValidationException("Некорретная дата релиза");
        }
        int id = getNextId();
        film.setId(id);
        films.put(id, film);
        log.info("POST запрос успешно обработан. Добавлен фильм: {}", film);
        return film;

    }

    @PutMapping
    public Film putFilm(@Valid @RequestBody Film film) {
        int id = film.getId();
        log.info("Поступил PUT запрос с телом: {}", film);
        if (films.containsKey(id)) {
            films.put(id, film);
            log.info("PUT запрос успешно обработан");
            return film;
        }
        log.warn("Фильм с таким id не найден");
        throw new NotFoundException("Фильм с таким id не найден");
    }

    private int getNextId() {
        return currentId++;
    }

}
