package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.BaseValidation;
import ru.yandex.practicum.filmorate.validation.PartialValidation;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> getAll() {
        log.info("Поступил запрос на получение списка всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getAllFriends(@PathVariable int id) {
        log.info("Поступил запрос на получение списка друзей пользователя с id: {}", id);
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Поступил запрос на получение общих друзей между пользователями с id: {}, {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping
    public User postUser(@Validated(BaseValidation.class) @RequestBody User user) {
        log.info("Поступил запрос на добавление пользователя с телом: {}", user);
        return userService.addUser(user);

    }

    @PutMapping
    public User putUser(@Validated(PartialValidation.class) @RequestBody User user) {
        log.info("Поступил запрос на обновление пользователя с телом: {}", user);
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Поступил запрос на добавление друга. id пользователя: {}, id добавляемого друга {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Поступил запрос на удаление друга. id пользователя: {}, id удаляемого друга {}", id, friendId);
        userService.deleteFriend(id, friendId);
    }
}
