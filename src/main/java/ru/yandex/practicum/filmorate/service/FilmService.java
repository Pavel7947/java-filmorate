package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final GenreService genreService;
    private final MpaService mpaService;
    private final UserService userService;

    public Film addFilm(Film film) {
        try {
            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                List<Integer> ids = film.getGenres().stream().map(Genre::getId).toList();
                genreService.getSeveral(ids);
            }
            if (film.getMpa() != null) {
                mpaService.getById(film.getMpa().getId());
            }
        } catch (NotFoundException e) {
            throw new BadRequestException(e.getMessage());
        }
        return filmRepository.addFilm(film);
    }

    public Film updateFilm(Film film) {
        getById(film.getId());
        try {
            if (film.getGenres() != null) {
                List<Integer> ids = film.getGenres().stream().map(Genre::getId).toList();
                genreService.getSeveral(ids);
            }
            if (film.getMpa() != null) {
                mpaService.getById(film.getMpa().getId());
            }
        } catch (NotFoundException e) {
            throw new BadRequestException(e.getMessage());
        }
        return filmRepository.updateFilm(film);
    }

    public List<Film> getAll() {
        return filmRepository.getAll();
    }

    public void addLike(int id, int userId) {
        getById(id);
        userService.getUser(userId);
        filmRepository.addLike(id, userId);
    }

    public Film getById(int id) {
        return filmRepository.getById(id).orElseThrow(() -> new NotFoundException("Фильм с id: " + id + " не найден"));
    }

    public void deleteLike(int filmId, int userId) {
        getById(filmId);
        userService.getUser(userId);
        filmRepository.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmRepository.getAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count).toList();
    }
}
