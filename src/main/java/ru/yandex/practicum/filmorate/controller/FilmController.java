package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Validated
@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;


    @GetMapping
    public Collection<Film> getAll() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable int id) {
        return filmService.getById(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@Positive(message = "Query параметр count не может быть отрицательным")
                                            @RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        filmService.deleteLike(id, userId);
    }

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film putFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }
}
