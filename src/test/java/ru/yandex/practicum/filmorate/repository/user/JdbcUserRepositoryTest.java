package ru.yandex.practicum.filmorate.repository.user;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import(JdbcUserRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JdbcUserRepositoryTest {
    private final JdbcUserRepository userRepository;
    private static final int TEST_USER_ID = 1;// This is the id of one of the existing users. USER_ID has 1 friend
    private static final int FRIEND_ID = 3; // This is the id of one of the existing users USER_ID AND FRIEND_ID no friend
    private static final int NUMBER_USERS = 3; // This is the number of movies in the database


    @Test
    public void testAddUser() {
        User testUser = getByTestUserId().toBuilder().name("otherName").build();
        User addedUser = userRepository.addUser(testUser);

        assertTrue(EqualsBuilder.reflectionEquals(testUser, addedUser, "id"),
                "Пользователи не совпадают");
    }

    @Test
    public void testUpdateUser() {
        User oldUser = getByTestUserId();
        userRepository.updateUser(oldUser.toBuilder().name("Valentina").build());
        User newUser = getByTestUserId();

        assertNotEquals(oldUser.getName(), newUser.getName(), "Имя не изменилось");
    }

    @Test
    public void testGetAll() {
        assertEquals(NUMBER_USERS, userRepository.getAll().size(), "Вернулось неверное количество пользователей");
    }


    @Test
    public void testGetSeveral() {
        List<Integer> idsTwoUsers = userRepository.getAll().stream().map(User::getId).limit(2).toList();
        assertEquals(2, userRepository.getSeveral(idsTwoUsers).size(),
                "Вернулось не 2 пользователя");
    }

    @Test
    public void testAddFriend() {
        User oldUser = getByTestUserId();
        userRepository.addFriend(TEST_USER_ID, FRIEND_ID);
        User newUser = getByTestUserId();
        assertNotEquals(oldUser.getFriends(), newUser.getFriends(), "Друг не добавился");
    }

    @Test
    public void testDeleteFriend() {
        User oldUser = getByTestUserId();
        userRepository.deleteFriend(TEST_USER_ID, oldUser.getFriends().values().stream().flatMap(Collection::stream)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("У пользователя с id: " + TEST_USER_ID + " нет друзей")));
        User newUser = getByTestUserId();
        assertNotEquals(oldUser.getFriends(), newUser.getFriends(), "Лайк не удалился");
    }

    private User getByTestUserId() {
        return userRepository.getById(TEST_USER_ID)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + TEST_USER_ID + " не найден"));

    }
}
