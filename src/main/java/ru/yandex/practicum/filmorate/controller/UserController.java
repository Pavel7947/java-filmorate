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
        return userService.getAllUsers();
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getAllFriends(@PathVariable int id) {
        userService.checkExistence(id);
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        userService.checkExistence(id);
        userService.checkExistence(otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping
    public User postUser(@Validated(BaseValidation.class) @RequestBody User user) {
        return userService.addUser(user);

    }

    @PutMapping
    public User putUser(@Validated(PartialValidation.class) @RequestBody User user) {
        userService.checkExistence(user.getId());
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.checkExistence(id);
        userService.checkExistence(friendId);
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.checkExistence(id);
        userService.checkExistence(friendId);
        return userService.deleteFriend(id, friendId);
    }
}
