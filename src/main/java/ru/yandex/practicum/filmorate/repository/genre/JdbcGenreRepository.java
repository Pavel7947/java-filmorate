package ru.yandex.practicum.filmorate.repository.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.mapper.GenreRowMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public List<Genre> getAll() {
        return jdbc.query("SELECT genre_id, name FROM genres", new GenreRowMapper());
    }

    @Override
    public List<Genre> getSeveral(List<Integer> ids) {
        return jdbc.query("SELECT genre_id, name FROM genres WHERE genre_id IN (:ids)", Map.of("ids", ids),
                new GenreRowMapper());
    }

    @Override
    public Optional<Genre> getById(int id) {
        return Optional.ofNullable(jdbc.query("SELECT genre_id, name FROM genres WHERE genre_id = :id",
                Map.of("id", id), rs -> {
                    Genre genre = null;
                    if (rs.next()) {
                        genre = Genre.builder()
                                .id(rs.getInt("genre_id"))
                                .name(rs.getString("name"))
                                .build();
                    }
                    return genre;
                }));
    }
}
