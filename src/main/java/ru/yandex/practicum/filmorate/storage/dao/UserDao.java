package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.Mapper;

import java.util.List;
import java.util.Optional;

@Repository("userDao")
public class UserDao implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public Optional<User> getUserById(long id) {
        String sqlQuery = "SELECT *\n" +
                "FROM users\n" +
                "WHERE id = ?;";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, Mapper::mapRowToUser, id));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<List<User>> getAllUsers() {
        String sqlQuery = "SELECT *\n" +
                "FROM users\n" +
                "ORDER BY id;";

        List<User> users = jdbcTemplate.query(sqlQuery, Mapper::mapRowToUser);

        return Optional.of(users);
    }

    @Override
    @Transactional
    public Optional<User> addUser(User user) {
        Long userId = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(user.toMap()).longValue();

        return getUserById(userId);
    }

    @Override
    @Transactional
    public Optional<User> updateUser(User user) {
        String sqlQuery = "UPDATE users\n" +
                "SET name = ?,\n" +
                "\tlogin = ?,\n" +
                "\temail = ?,\n" +
                "\tbirthday = ?\n" +
                "WHERE id = ?;";

        try {
            jdbcTemplate.update(sqlQuery,
                    user.getName(),
                    user.getLogin(),
                    user.getEmail(),
                    user.getBirthday(),
                    user.getId());
        } catch (DataAccessException e) {
            return Optional.empty();
        }

        return getUserById(user.getId());
    }

    @Override
    @Transactional
    public Optional<List<User>> getFriends(Long id) {
        String sqlQuery = "SELECT u.id AS id,\n" +
                "\tu.name AS name,\n" +
                "\tu.login AS login,\n" +
                "\tu.email AS email,\n" +
                "\tu.birthday AS birthday,\n" +
                "FROM users AS u\n" +
                "JOIN user_friends AS uf ON u.id = uf.friend_id\n" +
                "WHERE uf.user_id = ?;";

        return Optional.of(jdbcTemplate.query(sqlQuery, Mapper::mapRowToUser, id));
    }

    @Override
    @Transactional
    public Optional<List<User>> addFriend(Long fromId, Long toId) {
        String sqlQuery = "INSERT INTO user_friends (user_id, friend_id)\n" +
                "VALUES (?, ?);";

        jdbcTemplate.update(sqlQuery, fromId, toId);

        return getFriends(fromId);
    }

    @Override
    @Transactional
    public Optional<List<User>> removeFriend(Long fromId, Long toId) {
        String sqlQuery = "DELETE FROM user_friends\n" +
                "WHERE (user_id = ? AND friend_id = ?)\n" +
                "\tOR (user_id = ? AND friend_id = ?);";

        jdbcTemplate.update(sqlQuery, fromId, toId, toId, fromId);

        return getFriends(fromId);
    }
}
