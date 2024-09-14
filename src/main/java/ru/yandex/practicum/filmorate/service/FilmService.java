package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addLike(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        film.getLikes().add(userId);
        return film;
    }

    public Film deleteLike(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        if (film.getLikes().remove(userId)) {
            return film;
        }
        log.warn("Фильм с id:{} не содержит в себе лайка от пользователя с id: {}", id, userId);
        throw new NotFoundException("Фильм с id: " + id + " не содержит в себе лайка от  пользователя с id: " + userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count).toList();
    }

    public void checkExistence(int id) {
        if (filmStorage.isExists(id)) {
            return;
        }
        log.warn("Фильм с id: {} не найден.", id);
        throw new NotFoundException("Фильм с id: " + id + " не найден");
    }
}
