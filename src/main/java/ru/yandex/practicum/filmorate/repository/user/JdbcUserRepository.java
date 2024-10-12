package ru.yandex.practicum.filmorate.repository.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.StatusFriendship;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcUserRepository implements UserRepository {
    private final NamedParameterJdbcOperations jdbc;

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
        List<User> users = jdbc.query(queryGetUsers, new UserRowMapper());
        String queryGetFriends = "SELECT user_id, friend_id, approved FROM friends";
        List<FriendShip> friendShips = jdbc.query(queryGetFriends, new FriendShipRowMapper());

        return setFriendRelations(users, friendShips);
    }

    @Override
    public Optional<User> getById(int id) {
        String queryGetUser = "SELECT u.user_id, email, login, name, f.friend_id, f.approved ,birthday FROM " +
                "users AS u LEFT OUTER JOIN friends AS f ON f.user_id = u.user_id WHERE u.user_id = :id";
        return Optional.ofNullable(jdbc.query(queryGetUser, Map.of("id", id), rs -> {
            User user = null;
            while (rs.next()) {
                if (user == null) {
                    user = User.builder().id(rs.getInt("user_id")).email(rs.getString("email"))
                            .login(rs.getString("login")).name(rs.getString("name"))
                            .birthday(rs.getDate("birthday").toLocalDate()).build();
                }
                boolean isApproved = rs.getBoolean("approved");
                int friendId = rs.getInt("friend_id");
                if (isApproved) {
                    user.getFriends().get(StatusFriendship.APPROVED).add(friendId);
                } else {
                    user.getFriends().get(StatusFriendship.DISAPPROVED).add(friendId);
                }
            }
            return user;
        }));
    }

    @Override
    public List<User> getSeveral(Collection<Integer> ids) {
        String queryGetUsers = "SELECT user_id, email, login, name, birthday FROM users WHERE user_id IN (:ids)";
        List<User> users = jdbc.query(queryGetUsers, Map.of("ids", ids), new UserRowMapper());
        String queryGetFriends = "SELECT user_id, friend_id, approved FROM friends WHERE user_id IN (:ids)";
        List<FriendShip> friendShips = jdbc.query(queryGetFriends, Map.of("ids", ids), new FriendShipRowMapper());

        return setFriendRelations(users, friendShips);
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

    private List<User> setFriendRelations(List<User> users, List<FriendShip> friendShips) {
        for (User user : users) {
            long userId = user.getId();
            Set<Integer> approvedFriends = user.getFriends().get(StatusFriendship.APPROVED);
            Set<Integer> disApprovedFriends = user.getFriends().get(StatusFriendship.DISAPPROVED);
            for (FriendShip friendShip : friendShips) {
                if (friendShip.getUserId() != userId) {
                    continue;
                }
                int friendId = friendShip.getFriendId();
                if (friendShip.getStatus() == StatusFriendship.APPROVED) {
                    approvedFriends.add(friendId);
                } else {
                    disApprovedFriends.add(friendId);
                }
            }
        }
        return users;
    }

    @Getter
    @Setter
    private static class FriendShip {
        private int userId;
        private int friendId;
        private StatusFriendship status;
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = User.builder().build();
            user.setId(rs.getInt("user_id"));
            user.setEmail(rs.getString("email"));
            user.setName(rs.getString("name"));
            user.setLogin(rs.getString("login"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        }
    }

    private static class FriendShipRowMapper implements RowMapper<FriendShip> {
        @Override
        public FriendShip mapRow(ResultSet rs, int rowNum) throws SQLException {
            FriendShip friendShip = new FriendShip();
            friendShip.setUserId(rs.getInt("user_id"));
            friendShip.setFriendId(rs.getInt("friend_id"));
            boolean isApproved = rs.getBoolean("approved");
            if (isApproved) {
                friendShip.setStatus(StatusFriendship.APPROVED);
            } else {
                friendShip.setStatus(StatusFriendship.DISAPPROVED);
            }
            return friendShip;
        }
    }
}

