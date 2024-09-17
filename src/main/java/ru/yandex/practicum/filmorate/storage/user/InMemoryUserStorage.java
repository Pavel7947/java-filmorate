package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users;
    private int currentId = 1;

    public InMemoryUserStorage() {
        this.users = new HashMap<>();
    }

    @Override
    public User addUser(User user) {
        int id = getNextId();
        user.setId(id);
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        users.put(id, user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        String email = user.getEmail();
        User oldUser = users.put(user.getId(), user);
        if (email == null || email.isBlank()) {
            user.setEmail(oldUser.getEmail());
        }
        user.setFriends(oldUser.getFriends());
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public Optional<User> getUser(int id) {
        return Optional.ofNullable(users.get(id));
    }

    private int getNextId() {
        return currentId++;
    }
}
