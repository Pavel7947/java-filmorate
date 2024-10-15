package ru.yandex.practicum.filmorate.repository.user;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import(JdbcUserRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JdbcUserRepositoryTest {
    private final JdbcUserRepository userRepository;
    private static final int TEST_USER_ID = 1;// This is the id of one of the existing users. USER_ID has 1 friend
    private static final int OTHER_USER_ID = 3; // This is the id of one of the existing users USER_ID AND FRIEND_ID no friend
    private static final int NUMBER_USERS = 3; // This is the number of movies in the database


    @Test
    public void testAddUser() {
        User testUser = getTestUserById().toBuilder().name("otherName").email("@mail.ru").build();
        User addedUser = userRepository.addUser(testUser);

        assertTrue(EqualsBuilder.reflectionEquals(testUser, addedUser, "id"),
                "Пользователи не совпадают");
    }

    @Test
    public void testUpdateUser() {
        User oldUser = getTestUserById();
        userRepository.updateUser(oldUser.toBuilder().name("Valentina").build());
        User newUser = getTestUserById();

        assertNotEquals(oldUser.getName(), newUser.getName(), "Имя не изменилось");
    }

    @Test
    public void testGetAll() {
        assertEquals(NUMBER_USERS, userRepository.getAll().size(), "Вернулось неверное количество пользователей");
    }

    @Test
    public void testAddFriend() {
        List<User> oldFriends = userRepository.getAllFriends(TEST_USER_ID);
        userRepository.addFriend(TEST_USER_ID, OTHER_USER_ID);
        List<User> newFriends = userRepository.getAllFriends(TEST_USER_ID);
        assertNotEquals(oldFriends, newFriends, "Друг не добавился");
    }


    @Test
    public void testDeleteFriend() {
        List<User> oldFriends = userRepository.getAllFriends(TEST_USER_ID);
        userRepository.deleteFriend(TEST_USER_ID, oldFriends.getFirst().getId());
        List<User> newFriends = userRepository.getAllFriends(TEST_USER_ID);
        assertNotEquals(oldFriends, newFriends, "Друг не удалился");
    }

    @Test
    public void testGetCommonFriend() {
        userRepository.addFriend(OTHER_USER_ID, 2);
        userRepository.getCommonFriends(TEST_USER_ID, OTHER_USER_ID);
        assertFalse(userRepository.getCommonFriends(TEST_USER_ID, OTHER_USER_ID).isEmpty());
    }


    private User getTestUserById() {
        return userRepository.getById(TEST_USER_ID)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + TEST_USER_ID + " не найден"));

    }
}
