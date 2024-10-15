package ru.yandex.practicum.filmorate.repository.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mapper.MpaRowMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcMpaRepository implements MpaRepository {
    private final NamedParameterJdbcOperations jdbc;


    @Override
    public List<Mpa> getAll() {
        return jdbc.query("SELECT rating_id, name FROM mpa_ratings", new MpaRowMapper());
    }

    @Override
    public Optional<Mpa> getById(int id) {
        return Optional.ofNullable(jdbc.query("SELECT rating_id, name FROM mpa_ratings WHERE rating_id = " +
                ":id", Map.of("id", id), rs -> {
            Mpa mpa = null;
            if (rs.next()) {
                mpa = Mpa.builder()
                        .id(rs.getInt("rating_id"))
                        .name(rs.getString("name"))
                        .build();
            }
            return mpa;
        }));
    }
}
