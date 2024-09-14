package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User addFriend(int userId, int friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        return user;
    }

    public User deleteFriend(int userId, int friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        return user;
    }

    public List<User> getAllFriends(int userId) {
        User user = userStorage.getUser(userId);
        return user.getFriends().stream().map(userStorage::getUser).toList();
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = userStorage.getUser(userId);
        User otherUser = userStorage.getUser(otherId);

        return user.getFriends().stream().filter(otherUser.getFriends()::contains).map(userStorage::getUser).toList();
    }

    public void checkExistence(int id) {
        if (userStorage.isExists(id)) {
            return;
        }
        log.warn("Пользователь с id: {} не найден", id);
        throw new NotFoundException("Пользователя с id: " + id + " не существует");
    }
}
