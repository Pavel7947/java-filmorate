package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepository;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;
    private final UserRepository userRepository;

    public Film addFilm(Film film) {
        checkMpaAndGenres(film);
        return filmRepository.addFilm(film);
    }

    public Film updateFilm(Film film) {
        getById(film.getId());
        checkMpaAndGenres(film);
        return filmRepository.updateFilm(film);
    }

    public List<Film> getAll() {
        return filmRepository.getAll();
    }

    public void addLike(int id, int userId) {
        getById(id);
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        filmRepository.addLike(id, userId);
    }

    public Film getById(int id) {
        return filmRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + id + " не найден"));
    }

    public void deleteLike(int filmId, int userId) {
        getById(filmId);
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        filmRepository.deleteLike(filmId, userId);
    }

    public List<Film> getTopPopularFilms(int count) {
        return filmRepository.getTopPopularFilms(count);
    }

    private void checkMpaAndGenres(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Integer> ids = film.getGenres().stream().map(Genre::getId).toList();
            List<Genre> findGenres = genreRepository.getSeveral(ids);
            if (findGenres.size() != ids.size()) {
                throw new BadRequestException("Передан неправильный список жанров. Некоторые жанры не удалось найти в базе");
            }
        }
        if (film.getMpa() != null) {
            mpaRepository.getById(film.getMpa().getId())
                    .orElseThrow(() -> new BadRequestException("Передан несуществующий в программе mpa рейтинг"));
        }
    }
}
