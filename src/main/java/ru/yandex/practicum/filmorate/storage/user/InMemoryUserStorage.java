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
        if (user.getFriends() == null) {
            user.setFriends(oldUser.getFriends());
        }
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUser(int id) {
        return users.get(id);
    }

    private int getNextId() {
        return currentId++;
    }

    @Override
    public boolean isExists(int id) {
        return users.containsKey(id);
    }
}
