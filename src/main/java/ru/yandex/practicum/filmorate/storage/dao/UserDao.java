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

@Repository("userDao")
public class UserDao implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public User getUserById(long id) {
        String sqlQuery = "SELECT *\n" +
                "FROM users\n" +
                "WHERE id = ?;";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, Mapper::mapRowToUser, id);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public List<User> getAllUsers() {
        String sqlQuery = "SELECT *\n" +
                "FROM users\n" +
                "ORDER BY id;";

        List<User> users = jdbcTemplate.query(sqlQuery, Mapper::mapRowToUser);

        return users;
    }

    @Override
    @Transactional
    public User addUser(User user) {
        Long userId = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(Mapper.userToMap(user)).longValue();

        return getUserById(userId);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
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
            return null;
        }

        return getUserById(user.getId());
    }

    @Override
    @Transactional
    public int removeUser(long userId) {
        String sqlQuery = "DELETE\n" +
                "FROM users\n" +
                "WHERE id = ?;";
        return jdbcTemplate.update(sqlQuery, userId);
    }

    @Override
    @Transactional
    public List<User> getFriends(Long id) {
        String sqlQuery = "SELECT u.id AS id,\n" +
                "\tu.name AS name,\n" +
                "\tu.login AS login,\n" +
                "\tu.email AS email,\n" +
                "\tu.birthday AS birthday,\n" +
                "FROM users AS u\n" +
                "JOIN user_friends AS uf ON u.id = uf.friend_id\n" +
                "WHERE uf.user_id = ?;";

        return jdbcTemplate.query(sqlQuery, Mapper::mapRowToUser, id);
    }

    @Override
    @Transactional
    public List<User> addFriend(Long fromId, Long toId) {
        String sqlQuery = "INSERT INTO user_friends (user_id, friend_id)\n" +
                "VALUES (?, ?);";

        jdbcTemplate.update(sqlQuery, fromId, toId);

        return getFriends(fromId);
    }

    @Override
    @Transactional
    public List<User> removeFriend(Long fromId, Long toId) {
        String sqlQuery = "DELETE FROM user_friends\n" +
                "WHERE (user_id = ? AND friend_id = ?)\n" +
                "\tOR (user_id = ? AND friend_id = ?);";

        jdbcTemplate.update(sqlQuery, fromId, toId, toId, fromId);

        return getFriends(fromId);
    }

    @Override
    public Long getLikesCount(Long id) {
        String sql = "SELECT count(*) \n"
                + "FROM film_users_likes \n"
                + "WHERE user_id = ?;";
        Long likesCount = null;
        try {
            likesCount = jdbcTemplate.queryForObject(sql, Long.class, id);
        } catch (DataAccessException ignore) {
        }

        return likesCount == null ? 0 : likesCount;
    }

    @Override
    public Long getUserIdWithMostCommonLikes(Long id) {
        String sql = "SELECT user_id \n"
                + "FROM film_users_likes \n"
                + "WHERE film_id IN ( \n"
                + "    \tSELECT film_id \n"
                + "    \tFROM film_users_likes\n "
                + "    \tWHERE user_id = ? \n"
                + ") \n"
                + "AND user_id != ?\n "
                + "GROUP BY user_id \n"
                + "ORDER BY COUNT(*) DESC\n "
                + "LIMIT 1;";
        try {
            return jdbcTemplate.queryForObject(sql, Long.class, id, id);
        } catch (DataAccessException ignore) {
            return null;
        }
    }
}