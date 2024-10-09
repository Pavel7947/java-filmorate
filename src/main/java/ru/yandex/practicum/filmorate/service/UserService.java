package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void addFriend(int userId, int friendId) {
        getUser(userId);
        getUser(friendId);
        userRepository.addFriend(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        getUser(userId);
        getUser(friendId);
        userRepository.deleteFriend(userId, friendId);
    }

    public List<User> getAllFriends(int userId) {
        User user = getUser(userId);
        Set<Integer> idsFriends = user.getFriends().values().stream().flatMap(Collection::stream)
                .collect(Collectors.toSet());
        return userRepository.getSeveralUsers(idsFriends);
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public User addUser(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }
        return userRepository.addUser(user);

    }

    public User updateUser(User user) {
        User oldUser = getUser(user.getId());
        String name = user.getName();
        if (name == null || name.isBlank())
            user.setName(oldUser.getName());
        String email = user.getEmail();
        if (email == null || email.isBlank()) {
            user.setEmail(oldUser.getEmail());
        }
        return userRepository.updateUser(user);
    }

    public User getUser(int id) {
        return userRepository.getUser(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден"));
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = getUser(userId);
        User otherUser = getUser(otherId);

        Set<Integer> friendsOtherUser = otherUser.getFriends().values().stream().flatMap(Collection::stream)
                .collect(Collectors.toSet());
        Set<Integer> friendsUser = user.getFriends().values().stream().flatMap(Collection::stream)
                .collect(Collectors.toSet());
        return userRepository.getSeveralUsers(friendsUser.stream().filter(friendsOtherUser::contains)
                .collect(Collectors.toSet()));
    }

}
