package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User addUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    Optional<User> getUser(int id);

    List<User> getSeveralUsers(Collection<Integer> ids);

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);
}
