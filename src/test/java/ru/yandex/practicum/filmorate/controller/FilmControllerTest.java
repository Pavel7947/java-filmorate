package ru.yandex.practicum.filmorate.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmControllerTest {
    Film film = Film.builder().name("Film").description("Very good film")
            .releaseDate(LocalDate.of(2016, 8, 30)).duration(3600).mpa(Mpa.builder().id(1)
                    .build()).build();

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void goodPostRequestMustBeProcessedSuccessfully() {
        int statusCode = restTemplate.postForEntity("http://localhost:" + port + "/films", film, Film.class)
                .getStatusCode().value();
        assertEquals(200, statusCode);
    }

    @Test
    public void postRequestWithoutNameWillNotBeSuccessful() {
        Film failFilm = film.toBuilder().name("").build();
        int statusCode = restTemplate.postForEntity("http://localhost:" + port + "/films", failFilm, List.class)
                .getStatusCode().value();
        assertEquals(400, statusCode);
    }

    @Test
    public void postRequestWithADescriptionOf201CharacterWillNotBeSuccessful() {
        Film failFilm = film.toBuilder().description(RandomStringUtils.random(250)).build();
        int statusCode = restTemplate.postForEntity("http://localhost:" + port + "/films", failFilm, List.class)
                .getStatusCode().value();
        assertEquals(400, statusCode);
    }

    @Test
    public void postRequestWithAZeroDurationWillNotBeSuccessful() {
        Film failFilm = film.toBuilder().duration(0).build();
        int statusCode = restTemplate.postForEntity("http://localhost:" + port + "/films", failFilm, List.class)
                .getStatusCode().value();
        assertEquals(400, statusCode);
    }

    @Test
    public void postRequestWithAnUnrealisticReleaseDateWillNotBeSuccessful() {
        Film failFilm = film.toBuilder().releaseDate(LocalDate.of(1600, 12, 5)).build();
        int statusCode = restTemplate.postForEntity("http://localhost:" + port + "/films", failFilm, List.class)
                .getStatusCode().value();
        assertEquals(400, statusCode);
    }
}

