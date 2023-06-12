package ru.yandex.practicum.filmorate.util;

import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Mapper {
    private Mapper() {
    }

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

    public static final Director mapRowToDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
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

    public static final Map<String, Object> reviewToMap(Review review) {
        return new HashMap<>() {{
            put("review_id", review.getReviewId());
            put("content", review.getContent());
            put("is_positive", review.getIsPositive());
            put("user_id", review.getUserId());
            put("film_id", review.getFilmId());
        }};
    }

    public static final Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .useful(rs.getLong("useful"))
                .build();
    }

    public static final Map<String, Object> directorToMap(Director director) {
        return new HashMap<>() {{
            put("id", director.getId());
            put("name", director.getName());
        }};
    }

    public static final Map<String, Object> feedToMap(Long userId, Long entityId, String eventType, String operation) {
        return new HashMap<>() {{
            put("time_stamp", Instant.now());
            put("user_id", userId);
            put("event_type", eventType);
            put("operation", operation);
            put("entity_id", entityId);
        }};
    }

    public static final FeedEvent mapRowToFeed(ResultSet resultSet, int rowNum) throws SQLException {
        return FeedEvent.builder()
                .eventId(resultSet.getLong("event_id"))
                .timestamp(resultSet.getTimestamp("time_stamp").getTime())
                .userId(resultSet.getLong("user_id"))
                .eventType(resultSet.getString("event_type"))
                .operation(resultSet.getString("operation"))
                .entityId(resultSet.getLong("entity_id"))
                .build();
    }
}
