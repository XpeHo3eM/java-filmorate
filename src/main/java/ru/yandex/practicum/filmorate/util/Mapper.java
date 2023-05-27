package ru.yandex.practicum.filmorate.util;

import ru.yandex.practicum.filmorate.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;

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
                Mpa.valueOf(rs.getString("rating")));
    }

    public static final Long mapRowToLikes(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("user_id");
    }

    public static final Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.valueOf(rs.getString("genre"));
    }

    public static final Mpa mapRowToRatingMPA(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.valueOf(rs.getString("rating"));
    }
}
