package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenreFilmRelation {
    private int filmId;
    private int genreId;
    private String genreName;
}
