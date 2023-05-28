package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.util.Mapper;

import java.util.List;

@Component
public class GenreDao implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getGenres() {
        String sqlQuery = "SELECT *\n" +
                "FROM genres;";

        return jdbcTemplate.query(sqlQuery, Mapper::mapRowToGenre);
    }

    @Override
    public Genre getGenre(Integer id) {
        String sqlQuery = "SELECT *\n" +
                "FROM genres\n" +
                "WHERE id = ?;";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, Mapper::mapRowToGenre, id);
        } catch (DataAccessException e) {
            return null;
        }
    }
}
