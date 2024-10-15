package ru.yandex.practicum.filmorate.repository.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.mapper.UserRowMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcUserRepository implements UserRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public List<User> getAllFriends(int userId) {
        return jdbc.query("SELECT user_id, email, login, name, birthday FROM users WHERE user_id IN " +
                        "(SELECT friend_id FROM friends WHERE user_id = :userId);", Map.of("userId", userId),
                new UserRowMapper());
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        String query = "SELECT user_id, email, login, name, birthday FROM users WHERE user_id IN " +
                "(SELECT friend_id FROM friends WHERE user_id = :otherId AND friend_id IN" +
                "(SELECT friend_id FROM friends WHERE user_id = :userId))";
        return jdbc.query(query, Map.of("otherId", otherId, "userId", userId), new UserRowMapper());

    }

    @Override
    public User addUser(User user) {
        String query = "INSERT INTO users (email, login, name, birthday) VALUES (:email, :login, :name, :birthday);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource params = new BeanPropertySqlParameterSource(user);
        jdbc.update(query, params, keyHolder, new String[]{"user_id"});
        user.setId(keyHolder.getKeyAs(Integer.class));
        return user;
    }

    @Override
    public User updateUser(User user) {
        String query = "UPDATE users SET email = :email, login = :login, name = :name, birthday = :birthday " +
                "WHERE user_id = :id";
        SqlParameterSource params = new BeanPropertySqlParameterSource(user);
        jdbc.update(query, params);
        return user;
    }

    @Override
    public List<User> getAll() {
        String queryGetUsers = "SELECT user_id, email, login, name, birthday FROM users";
        return jdbc.query(queryGetUsers, new UserRowMapper());
    }

    @Override
    public Optional<User> getById(int id) {
        String queryGetUser = "SELECT user_id, email, login, name, birthday FROM users WHERE user_id = :id";
        return Optional.ofNullable(jdbc.query(queryGetUser, Map.of("id", id), rs -> {
            User user = null;
            while (rs.next()) {
                if (user == null) {
                    user = User.builder().id(rs.getInt("user_id")).email(rs.getString("email"))
                            .login(rs.getString("login")).name(rs.getString("name"))
                            .birthday(rs.getDate("birthday").toLocalDate()).build();
                }
            }
            return user;
        }));
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String queryUpdate = "UPDATE friends SET approved = true WHERE friend_id = :userId AND user_id = :friendId;";
        Map<String, Object> params = new HashMap<>(Map.of("userId", userId, "friendId", friendId));
        int countUpdated = jdbc.update(queryUpdate, params);
        String queryInsert = "INSERT INTO friends (user_id, friend_id, approved) VALUES (:userId, :friendId, :approved);";
        params.put("approved", countUpdated > 0);

        jdbc.update(queryInsert, params);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        Map<String, Object> params = Map.of("userId", userId, "friendId", friendId);
        int count = jdbc.update("DELETE FROM friends WHERE user_id = :userId AND friend_id = :friendId", params);
        if (count < 0) {
            log.warn("Пользователь с id: {} не содержит в своем списке друзей пользователя с id: {}", userId, friendId);
            throw new NotFoundException("Пользователь с Id: " + userId + " не содержит в своем списке друзей" +
                    " пользователя с id: " + friendId);
        }
        jdbc.update("UPDATE friends SET approved = false WHERE friend_id = :userId AND user_id = :friendId;", params);
    }
}

