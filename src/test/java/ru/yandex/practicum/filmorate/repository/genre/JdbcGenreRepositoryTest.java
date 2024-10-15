package ru.yandex.practicum.filmorate.repository.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@Import(JdbcGenreRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JdbcGenreRepositoryTest {
    private final JdbcGenreRepository genreRepository;
    private static final int TEST_GENRE_ID = 1; // This is the id of one of the existing genres
    private static final int NUMBER_GENRE = 6; // This is the number of  genres in the database

    @Test
    public void testGetAll() {
        assertEquals(NUMBER_GENRE, genreRepository.getAll().size(), "Вернулось неверное количество жанров");
    }

    @Test
    public void testGetById() {
        genreRepository.getById(TEST_GENRE_ID)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id: " + TEST_GENRE_ID + " не найден"));
    }

    @Test
    public void testGetSeveral() {
        List<Integer> idsTwoGenres = genreRepository.getAll().stream().map(Genre::getId).limit(2).toList();
        assertEquals(2, genreRepository.getSeveral(idsTwoGenres).size(),
                "Вернулось не 2 жанра");
    }
}