package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User addFriend(int userId, int friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        return user;
    }

    public User deleteFriend(int userId, int friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        return user;
    }

    public List<User> getAllFriends(int userId) {
        User user = getUser(userId);
        return user.getFriends().stream().map(this::getUser).toList();
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        getUser(user.getId());
        return userStorage.updateUser(user);
    }

    public User getUser(int id) {
        return userStorage.getUser(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден"));
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = getUser(userId);
        User otherUser = getUser(otherId);

        return user.getFriends().stream().filter(otherUser.getFriends()::contains).map(this::getUser).toList();
    }
}
