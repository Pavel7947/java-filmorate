package ru.yandex.practicum.filmorate.repository.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@Import(JdbcMpaRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JdbcMpaRepositoryTest {
    private final JdbcMpaRepository mpaRepository;
    private static final int TEST_MPA_ID = 1; // This is the id of one of the existing mpa ratings
    private static final int NUMBER_MPA = 5; // This is the number of mpa ratings in the database

    @Test
    public void testGetAll() {
        assertEquals(NUMBER_MPA, mpaRepository.getAll().size(), "Вернулось неверное количество фильмов");
    }

    @Test
    public void testGetById() {
        mpaRepository.getById(TEST_MPA_ID)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id: " + TEST_MPA_ID + " не найден"));
    }

}
