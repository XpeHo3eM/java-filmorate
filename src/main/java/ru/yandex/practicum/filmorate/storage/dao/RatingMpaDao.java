package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.RatingMpaStorage;
import ru.yandex.practicum.filmorate.util.Mapper;

import java.util.List;
import java.util.Optional;

@Component
public class RatingMpaDao implements RatingMpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public RatingMpaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<List<Mpa>> getRatingMPAs() {
        String sqlQuery = "SELECT *\n" +
                "FROM ratingsMPA;";

        return Optional.of(jdbcTemplate.query(sqlQuery, Mapper::mapRowToRatingMPA));
    }

    @Override
    public Optional<Mpa> getRatingMPA(Integer id) {
        String sqlQuery = "SELECT *\n" +
                "FROM ratingsMPA\n" +
                "WHERE id = ?;";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, Mapper::mapRowToRatingMPA, id));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }
}
