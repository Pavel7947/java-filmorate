package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private int currentId = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        log.info("Поступил Get запрос на получение списка всех пользователей");
        return users.values();
    }

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        log.info("Поступил Post запрос с телом: {}", user);
        String name = user.getName();
        int id = getNextId();
        user.setId(id);
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(id, user);
        log.info("Запрос успешно обработан. Добавлен пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) {
        log.info("Поступил Put запрос с телом: {}", user);
        int id = user.getId();
        if (users.containsKey(id)) {
            users.put(id, user);
            log.info("Put запрос успешно обработан");
            return user;
        }
        log.warn("Пользователь с таким id не найден");
        throw new NotFoundException("Пользователь с таким id не найден");
    }

    private int getNextId() {
        return currentId++;
    }


}