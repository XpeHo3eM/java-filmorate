package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public FeedDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createFeed(Long userId, Long entityId, String eventType, String operation) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("feeds")
                .usingGeneratedKeyColumns("event_id");
        simpleJdbcInsert.executeAndReturnKey(Mapper.feedToMap(userId, entityId, eventType, operation)).longValue();
    }

    @Override
    public List<FeedEvent> getFeedByUserId(Long userId) {
        String sqlQuery = "SELECT *\n" +
                "FROM feeds\n" +
                "WHERE user_id = ?";
        return jdbcTemplate.query(sqlQuery, Mapper::mapRowToFeed, userId);
    }
}
