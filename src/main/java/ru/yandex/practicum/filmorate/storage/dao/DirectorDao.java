package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.util.Mapper;

import java.util.List;

@Repository("directorDao")
public class DirectorDao implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    public DirectorDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public Director getDirectorById(long id) {
        String sqlQuery = "SELECT *\n" +
                "FROM directors AS d\n" +
                "WHERE id = ?;";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, Mapper::mapRowToDirector, id);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public List<Director> getAllDirectors() {
        String sqlQuery = "SELECT *\n" +
                "FROM directors\n" +
                "ORDER BY id;";

        return jdbcTemplate.query(sqlQuery, Mapper::mapRowToDirector);
    }

    @Override
    @Transactional
    public Director addDirector(Director director) {
        long directorId = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(Mapper.directorToMap(director)).longValue();

        return getDirectorById(directorId);
    }

    @Override
    @Transactional
    public Director updateDirector(Director director) {
        String sqlQuery = "UPDATE directors\n" +
                "SET name = ?\n" +
                "WHERE id = ?;";

        try {
            jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        } catch (DataAccessException e) {
            return null;
        }

        return getDirectorById(director.getId());
    }

    @Override
    @Transactional
    public Director removeDirector(long id) {
        String sqlQuery = "DELETE FROM directors\n" +
                "WHERE id = ?;";

        try {
            jdbcTemplate.update(sqlQuery, id);
        } catch (DataAccessException e) {
            return null;
        }

        return getDirectorById(id);
    }
}
