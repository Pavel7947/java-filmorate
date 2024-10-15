package ru.yandex.practicum.filmorate.repository.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepository {

    List<Genre> getAll();

    List<Genre> getSeveral(List<Integer> ids);

    Optional<Genre> getById(int id);
}
