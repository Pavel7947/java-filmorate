package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films;
    private int currentId = 1;

    public InMemoryFilmStorage() {
        this.films = new HashMap<>();
    }

    @Override
    public Film addFilm(Film film) {
        int id = getNextId();
        film.setId(id);
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        films.put(id, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Film oldFilm = films.put(film.getId(), film);
        if (film.getLikes() == null) {
            film.setLikes(oldFilm.getLikes());
        }
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film getFilm(int id) {
        return films.get(id);
    }

    @Override
    public boolean isExists(int id) {
        return films.containsKey(id);
    }

    private int getNextId() {
        return currentId++;
    }
}
