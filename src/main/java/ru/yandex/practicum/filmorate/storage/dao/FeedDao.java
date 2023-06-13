package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.util.Mapper;

import java.util.List;

@Component
public class FeedDao implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    public FeedDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createFeed(Long userId, Long entityId, String eventType, String operation) {
        new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("feeds")
                .usingGeneratedKeyColumns("event_id")
                .execute(Mapper.feedToMap(userId, entityId, eventType, operation));
    }

    @Override
    public List<FeedEvent> getFeedByUserId(Long userId) {
        String sqlQuery = "SELECT *\n" +
                "FROM feeds\n" +
                "WHERE user_id = ?";

        return jdbcTemplate.query(sqlQuery, Mapper::mapRowToFeed, userId);
    }
}
