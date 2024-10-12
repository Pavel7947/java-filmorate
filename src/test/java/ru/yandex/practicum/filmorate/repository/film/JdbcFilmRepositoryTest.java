package ru.yandex.practicum.filmorate.repository.film;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import static org.junit.jupiter.api.Assertions.*;


@JdbcTest
@Import(JdbcFilmRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JdbcFilmRepositoryTest {
    private final JdbcFilmRepository filmRepository;
    private static final int TEST_FILM_ID = 1; // This is the id of one of the existing movies that contains likes
    private static final int TEST_USER_ID = 1; // This is the id of one of the existing users
    private static final int NUMBER_FILMS = 3; // This is the number of movies in the database

    @Test
    public void testAddFilm() {
        Film testFilm = getByTestFilmId().toBuilder().name("otherName").build();
        Film addedFilm = filmRepository.addFilm(testFilm);

        assertTrue(EqualsBuilder.reflectionEquals(testFilm, addedFilm, "id"), "Фильмы не совпадают");
    }

    @Test
    public void testGetAll() {
        assertEquals(NUMBER_FILMS, filmRepository.getAll().size(), "Вернулось неверное количество фильмов");
    }


    @Test
    public void testUpdateFilm() {
        Film oldFilm = getByTestFilmId();
        filmRepository.updateFilm(oldFilm.toBuilder().name("Terminator").build());
        Film newFilm = getByTestFilmId();
        assertNotEquals(oldFilm.getName(), newFilm.getName(), "Имя не изменилось");
    }

    @Test
    public void testAddLike() {
        Film oldFilm = getByTestFilmId();
        filmRepository.addLike(TEST_FILM_ID, TEST_USER_ID);
        Film newFilm = getByTestFilmId();
        assertNotEquals(oldFilm.getLikes(), newFilm.getLikes(), "Лайк не добавился");
    }

    @Test
    public void testDeleteLike() {
        Film oldFilm = getByTestFilmId();
        filmRepository.deleteLike(TEST_FILM_ID, oldFilm.getLikes().stream().toList().getFirst());
        Film newFilm = getByTestFilmId();
        assertNotEquals(oldFilm.getLikes(), newFilm.getLikes(), "Лайк не удалился");
    }

    private Film getByTestFilmId() {
        return filmRepository.getById(TEST_FILM_ID)
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + TEST_FILM_ID + " не найден"));
    }

}
