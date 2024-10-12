package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class GenreService {
    private final GenreRepository genreRepository;

    public List<Genre> getSeveral(List<Integer> ids) {
        List<Genre> genres = genreRepository.getSeveral(ids);
        if (genres.size() != ids.size()) {
            List<Integer> notFoundGenres = genres.stream().map(Genre::getId).filter(id -> !ids.contains(id)).toList();
            log.warn("Не удалось найти некоторые жанры в базе данных {}", notFoundGenres);
            throw new NotFoundException("Не удалось найти некоторые жанры в базе данных " + notFoundGenres);
        }
        return genres;
    }

    public List<Genre> getAll() {
        return genreRepository.getAll();
    }

    public Genre getById(int id) {
        return genreRepository.getById(id).orElseThrow(() -> new NotFoundException("Жанр с id: " + id +
                " не существует"));
    }
}
