package ru.yandex.practicum.filmorate.repository.film;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;


public interface FilmRepository {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAll();

    Optional<Film> getById(int id);

    void addLike(int filmId, int userId);

    List<Integer> getListAllLikes(int filmId);

    void deleteLike(int filmId, int userId);

    List<Film> getTopPopularFilms(int count);
}
