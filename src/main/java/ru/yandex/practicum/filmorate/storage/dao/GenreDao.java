package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.util.Mapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class GenreDao implements GenreStorage {
    private static JdbcTemplate jdbcTemplate;

    public GenreDao(JdbcTemplate jdbcTemplate) {
        GenreDao.jdbcTemplate = jdbcTemplate;
    }

    public static Map<String, Genre> getGenreNameToGenreMap() {
        String sqlQuery = "SELECT *\n" +
                "FROM genres;";

        List<Genre> genres = jdbcTemplate.query(sqlQuery, Mapper::mapRowToGenre);

        Map<String, Genre> genresMap = new LinkedHashMap<>();
        genres.forEach(genre -> genresMap.put(genre.getName(), genre));

        return genresMap;
    }

    @Override
    public List<Genre> getGenres() {
        return new ArrayList<>(getGenreNameToGenreMap().values());
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
