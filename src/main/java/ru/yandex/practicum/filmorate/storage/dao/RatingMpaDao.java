package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.RatingMpaStorage;
import ru.yandex.practicum.filmorate.util.Mapper;

import java.util.List;

@Component
public class RatingMpaDao implements RatingMpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public RatingMpaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getRatingMPAs() {
        String sqlQuery = "SELECT *\n" +
                "FROM ratingsMPA;";

        return jdbcTemplate.query(sqlQuery, Mapper::mapRowToRatingMPA);
    }

    @Override
    public Mpa getRatingMPA(Integer id) {
        String sqlQuery = "SELECT *\n" +
                "FROM ratingsMPA\n" +
                "WHERE id = ?;";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, Mapper::mapRowToRatingMPA, id);
        } catch (DataAccessException e) {
            return null;
        }
    }
}
