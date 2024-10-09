package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserService userService;

    public Film addFilm(Film film) {
        return filmRepository.addFilm(film);
    }

    public Film updateFilm(Film film) {
        Film oldFilm = getFilm(film.getId());
        film.getLikes().addAll(oldFilm.getLikes());
        return filmRepository.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmRepository.getAllFilms();
    }

    public Film addLike(int id, int userId) {
        Film film = getFilm(id);
        userService.getUser(userId);
        film.getLikes().add(userId);
        return film;
    }

    public Film getFilm(int id) {
        return filmRepository.getFilm(id).orElseThrow(() -> new NotFoundException("Фильм с id: " + id + " не найден"));
    }

    public Film deleteLike(int id, int userId) {
        Film film = getFilm(id);
        if (film.getLikes().remove(userId)) {
            return film;
        }
        log.warn("Фильм с id:{} не содержит в себе лайка от пользователя с id: {}", id, userId);
        throw new NotFoundException("Фильм с id: " + id + " не содержит в себе лайка от  пользователя с id: " + userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmRepository.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count).toList();
    }
}
