package ru.yandex.practicum.filmorate.repository.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.GenreFilmRelation;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.repository.mapper.GenreFilmRelationRowMapper;

import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcFilmRepository implements FilmRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public List<Film> getTopPopularFilms(int count) {
        String queryGetFilms = "SELECT f.film_id, f.name AS film_name, description, release_date, duration, " +
                "f.rating_id, mr.name AS mpa_name FROM films AS f " +
                "JOIN mpa_ratings AS mr ON mr.rating_id = f.rating_id " +
                "JOIN (SELECT film_id, COUNT(user_id) as count_like FROM likes GROUP BY film_id ORDER by count_like DESC " +
                "LIMIT :count) AS l ON l.film_id = f.film_id ORDER by count_like DESC;";

        String queryGetGenre = "SELECT fg.film_id, fg.genre_id, name FROM films_genres AS fg " +
                "JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "JOIN (SELECT film_id, COUNT(user_id) as count_like FROM likes GROUP BY film_id ORDER by count_like DESC " +
                "LIMIT :count) AS l ON l.film_id = fg.film_id ORDER by count_like DESC;";

        List<Film> films = jdbc.query(queryGetFilms, Map.of("count", count), new FilmRowMapper());
        List<GenreFilmRelation> genreRelations = jdbc.query(queryGetGenre, Map.of("count", count),
                new GenreFilmRelationRowMapper());

        for (Film film : films) {
            Set<Genre> genres = film.getGenres();
            int filmId = film.getId();
            for (GenreFilmRelation relation : genreRelations) {
                if (relation.getFilmId() != filmId) {
                    continue;
                }
                genres.add(Genre.builder().id(relation.getGenreId()).name(relation.getGenreName()).build());
            }
        }
        return films;
    }

    @Override
    public void addLike(int filmId, int userId) {
        jdbc.update("INSERT INTO likes (film_id, user_id) VALUES (:filmId, :userId);",
                Map.of("filmId", filmId, "userId", userId));
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        int count = jdbc.update("DELETE FROM likes WHERE film_id = :filmId AND user_id = :userId",
                Map.of("filmId", filmId, "userId", userId));
        if (count < 1) {
            log.warn("Фильм с id: {} не содержит в себе лайка от пользователя с id: {}", filmId, userId);
            throw new NotFoundException("Фильм с id: " + filmId + " не содержит в себе лайка от  пользователя с id: "
                    + userId);
        }
    }

    @Override
    public Film addFilm(Film film) {
        String queryInsertFilm = "INSERT INTO films (name, description, release_date, duration, rating_id ) " +
                "VALUES (:name, :description, :release_date, :duration, :rating_id);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource params = new MapSqlParameterSource(Map.of("name", film.getName(),
                "description", film.getDescription(), "release_date", film.getReleaseDate(),
                "duration", film.getDuration(),
                "rating_id", film.getMpa().getId()));
        jdbc.update(queryInsertFilm, params, keyHolder, new String[]{"film_id"});
        int filmId = keyHolder.getKeyAs(Integer.class);
        film.setId(filmId);
        insertFilmsGenres(film);
        return getById(filmId).get();
    }

    @Override
    public Film updateFilm(Film film) {
        String queryUpdate = "UPDATE films SET name = :name, description = :description, release_date = :releaseDate, " +
                "duration = :duration, rating_id = :ratingId WHERE film_id = :id";
        Map<String, Object> params = Map.of("name", film.getName(),
                "description", film.getDescription(), "releaseDate", film.getReleaseDate(),
                "duration", film.getDuration(),
                "ratingId", film.getMpa().getId(),
                "id", film.getId());
        jdbc.update(queryUpdate, params);
        jdbc.update("DELETE FROM films_genres WHERE film_id = :id", params);
        insertFilmsGenres(film);
        return film;
    }

    @Override
    public List<Film> getAll() {
        String queryGetAllFilms = "SELECT film_id, f.name AS film_name, description, release_date, duration, " +
                "f.rating_id, mr.name AS mpa_name FROM films AS f " +
                "JOIN mpa_ratings AS mr ON mr.rating_id = f.rating_id";
        String queryGetAllGenreRelations = "SELECT film_id, fg.genre_id, name FROM films_genres AS fg " +
                "JOIN genres AS g ON fg.genre_id = g.genre_id";

        List<Film> films = jdbc.query(queryGetAllFilms, new FilmRowMapper());
        List<GenreFilmRelation> genreRelations = jdbc.query(queryGetAllGenreRelations, new GenreFilmRelationRowMapper());

        for (Film film : films) {
            Set<Genre> genres = film.getGenres();
            int filmId = film.getId();
            for (GenreFilmRelation relation : genreRelations) {
                if (relation.getFilmId() != filmId) {
                    continue;
                }
                genres.add(Genre.builder().id(relation.getGenreId()).name(relation.getGenreName()).build());
            }
        }
        return films;


    }

    @Override
    public Optional<Film> getById(int id) {
        String queryGetFilm = "SELECT f.film_id, f.name AS film_name, g.name AS genre_name, r.name AS rating_name, " +
                "description, duration, release_date, f.rating_id, g.genre_id  FROM films AS f " +
                "LEFT OUTER JOIN films_genres AS fg ON fg.film_id = f.film_id " +
                "LEFT OUTER JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "LEFT OUTER JOIN mpa_ratings AS r ON r.rating_id = f.rating_id " +
                "WHERE f.film_id = :id " +
                "ORDER BY fg.genre_id";
        return Optional.ofNullable(jdbc.query(queryGetFilm, Map.of("id", id), rs -> {
            Film film = null;
            while (rs.next()) {
                if (film == null) {
                    film = Film.builder().id(rs.getInt("film_id"))
                            .name(rs.getString("name"))
                            .description(rs.getString("description"))
                            .duration(rs.getInt("duration"))
                            .releaseDate(rs.getDate("release_date").toLocalDate())
                            .mpa(Mpa.builder().id(rs.getInt("rating_id"))
                                    .name(rs.getString("rating_name")).build())
                            .genres(new LinkedHashSet<>())
                            .build();
                }
                int genreId = rs.getInt("genre_id");
                if (genreId != 0) {
                    film.getGenres().add(Genre.builder().id(genreId).name(rs.getString("genre_name")).build());
                }
            }
            return film;
        }));

    }


    @Override
    public List<Integer> getListAllLikes(int filmId) {
        return jdbc.query("SELECT user_id FROM likes WHERE film_id = :filmId", Map.of("filmId", filmId), rs -> {
            List<Integer> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getInt("user_id"));
            }
            return ids;
        });
    }

    private void insertFilmsGenres(Film film) {
        int filmId = film.getId();
        Set<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            Map<String, Object>[] batchValues = new Map[genres.size()];
            String queryInsertFilmGenres = "INSERT INTO films_genres (genre_id, film_id) VALUES (:genre_id, :film_id);";
            int count = 0;
            for (Genre genre : genres) {
                batchValues[count] = Map.of("genre_id", genre.getId(), "film_id", filmId);
                count++;
            }
            jdbc.batchUpdate(queryInsertFilmGenres, batchValues);
        }


    }
}
