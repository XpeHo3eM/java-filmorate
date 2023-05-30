package ru.yandex.practicum.filmorate.util;

import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Mapper {
    public static final User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    public static final Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                MpaDao.getMpaNameToMpaMap().get(rs.getString("rating")));
    }

    public static final Long mapRowToLikes(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("user_id");
    }

    public static final Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("id"), rs.getString("genre"));
    }

    public static final Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("id"), rs.getString("rating"));
    }

    public static final Map<String, Object> userToMap(User user) {
        return new HashMap<>() {{
            put("id", user.getId());
            put("email", user.getEmail());
            put("login", user.getLogin());
            put("name", user.getName());
            put("birthday", user.getBirthday());
        }};
    }

    public static final Map<String, Object> filmToMap(Film film) {
        return new HashMap<>() {{
            put("id", film.getId());
            put("name", film.getName());
            put("description", film.getDescription());
            put("release_date", film.getReleaseDate());
            put("duration", film.getDuration());
            put("rating_id", film.getMpa().getId());
        }};
    }
}
