package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.util.Mapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class MpaDao implements MpaStorage {
    private static JdbcTemplate jdbcTemplate;

    public MpaDao(JdbcTemplate jdbcTemplate) {
        MpaDao.jdbcTemplate = jdbcTemplate;
    }

    public static Map<String, Mpa> getMpaNameToMpaMap() {
        String sqlQuery = "SELECT *\n" +
                "FROM mpas;";

        List<Mpa> mpas = jdbcTemplate.query(sqlQuery, Mapper::mapRowToMpa);

        Map<String, Mpa> mpasMap = new LinkedHashMap<>();
        mpas.forEach(mpa -> mpasMap.put(mpa.getName(), mpa));

        return mpasMap;
    }

    @Override
    public List<Mpa> getMpas() {
        return new ArrayList<>(getMpaNameToMpaMap().values());
    }

    @Override
    public Mpa getMpa(Integer id) {
        String sqlQuery = "SELECT *\n" + "FROM mpas\n" + "WHERE id = ?;";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, Mapper::mapRowToMpa, id);
        } catch (DataAccessException e) {
            return null;
        }
    }
}
