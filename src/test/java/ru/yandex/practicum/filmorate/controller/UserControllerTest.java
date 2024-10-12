package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    User user = User.builder().name("Pasha").login("syutkinpi").email("syutkinpi@mail.ru")
            .birthday(LocalDate.of(2000, Month.MARCH, 2)).build();

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void postRequestWithTheCorrectBodyShouldReturnCode200() {
        int statusCode = restTemplate.postForEntity("http://localhost:" + port + "/users", user, User.class)
                .getStatusCode().value();
        assertEquals(200, statusCode);
    }

    @Test
    public void postRequestWithoutALoginWillNotBeSuccessful() {
        User failUser = user.toBuilder().login("").build();
        int statusCode = restTemplate.postForEntity("http://localhost:" + port + "/users", failUser, User.class)
                .getStatusCode().value();
        assertEquals(400, statusCode);
    }

    @Test
    public void postRequestNullLoginWillNotBeSuccessful() {
        User failUser = user.toBuilder().login(null).build();
        int statusCode = restTemplate.postForEntity("http://localhost:" + port + "/users", failUser, User.class)
                .getStatusCode().value();
        assertEquals(400, statusCode);
    }

    @Test
    public void postRequestWithoutAEmailWillNotBeSuccessful() {
        User failUser = user.toBuilder().email("").build();
        int statusCode = restTemplate.postForEntity("http://localhost:" + port + "/users", failUser, User.class)
                .getStatusCode().value();
        assertEquals(400, statusCode);
    }

    @Test
    public void postRequestNullEmailWillNotBeSuccessful() {
        User failUser = user.toBuilder().email(null).build();
        int statusCode = restTemplate.postForEntity("http://localhost:" + port + "/users", failUser, User.class)
                .getStatusCode().value();
        assertEquals(400, statusCode);
    }

    @Test
    public void postRequestBirthdayInTheFutureWillNotBeSuccessful() {
        User failUser = user.toBuilder().birthday(LocalDate.of(2025, 8, 30)).build();
        int statusCode = restTemplate.postForEntity("http://localhost:" + port + "/users", failUser, User.class)
                .getStatusCode().value();
        assertEquals(400, statusCode);
    }
}

