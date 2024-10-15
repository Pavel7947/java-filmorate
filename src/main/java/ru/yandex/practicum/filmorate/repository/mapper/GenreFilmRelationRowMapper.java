package ru.yandex.practicum.filmorate.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.GenreFilmRelation;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreFilmRelationRowMapper implements RowMapper<GenreFilmRelation> {
    @Override
    public GenreFilmRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
        GenreFilmRelation relation = new GenreFilmRelation();
        relation.setFilmId(rs.getInt("film_id"));
        relation.setGenreId(rs.getInt("genre_id"));
        relation.setGenreName(rs.getString("name"));
        return relation;

    }
}
