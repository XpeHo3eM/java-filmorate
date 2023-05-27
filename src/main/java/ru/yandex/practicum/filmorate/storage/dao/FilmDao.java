package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.util.Mapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository("filmDao")
public class FilmDao implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public Optional<Film> getFilmById(long id) {
        String sqlQuery = "SELECT f.id,\n" +
                "\tf.name,\n" +
                "\tf.description,\n" +
                "\tf.release_date,\n" +
                "\tf.duration,\n" +
                "\tr.rating\n" +
                "FROM films AS f\n" +
                "JOIN ratingsMPA AS r ON f.rating_id = r.id\n" +
                "WHERE f.id = ?;";

        Film film;

        try {
            film = jdbcTemplate.queryForObject(sqlQuery, Mapper::mapRowToFilm, id);
        } catch (DataAccessException e) {
            return Optional.empty();
        }

        film.setGenres(getGenres(id));
        film.setUsersLikes(getLikes(id));

        return Optional.of(film);
    }

    @Override
    @Transactional
    public Optional<List<Film>> getAllFilms() {
        String sqlQuery = "SELECT f.id,\n" +
                "\tf.name,\n" +
                "\tf.description,\n" +
                "\tf.release_date,\n" +
                "\tf.duration,\n" +
                "\tr.rating\n" +
                "FROM films AS f\n" +
                "JOIN ratingsMPA AS r ON f.rating_id = r.id\n" +
                "ORDER BY f.id;";

        List<Film> films = jdbcTemplate.query(sqlQuery, Mapper::mapRowToFilm);
        Map<Long, Film> filmsMap = films.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));

        List<Map<String, Object>> genres = getGenres();

        getGenres().forEach(row -> {
            filmsMap.get(Long.parseLong(row.get("film_id").toString())).getGenres()
                    .add(Genre.valueOf(row.get("genre").toString()));
        });
        getLikes().forEach(row -> {
            filmsMap.get(Long.parseLong(row.get("film_id").toString())).getUsersLikes()
                    .add(Long.parseLong(row.get("user_id").toString()));
        });

        return Optional.of(films);
    }

    @Override
    @Transactional
    public Optional<Film> addFilm(Film film) {
        Long filmId = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(film.toMap())
                .longValue();

        film.setId(filmId);

        addFilmGenres(film);
        addFilmUsersLikes(film);

        return getFilmById(filmId);
    }

    @Override
    @Transactional
    public Optional<Film> updateFilm(Film film) {
        if (getFilmById(film.getId()).isEmpty()) {
            return Optional.empty();
        }

        try {
            updateFilmsTable(film);
        } catch (DataAccessException e) {
            return Optional.empty();
        }

        updateFilmGenres(film);
        updateFilmLikes(film);

        return getFilmById(film.getId());
    }

    private void addFilmGenres(Film film) {
        String sqlQuery = "INSERT INTO film_genres(film_id, genre_id)\n" +
                "VALUES(?, ?);";

        List<Genre> genres = new ArrayList<>(film.getGenres());
        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setLong(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }

    private void addFilmUsersLikes(Film film) {
        String sqlQuery = "INSERT INTO film_users_likes (film_id, user_id)\n" +
                "VALUES(?, ?);";

        List<Long> likes = new ArrayList<>(film.getUsersLikes());
        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setLong(2, likes.get(i));
            }

            @Override
            public int getBatchSize() {
                return likes.size();
            }
        });
    }

    private void updateFilmsTable(Film film) {
        String sqlQuery = "UPDATE films\n" +
                "SET name = ?,\n" +
                "\tdescription = ?,\n" +
                "\trelease_date = ?,\n" +
                "\tduration = ?,\n" +
                "\trating_id = ?\n" +
                "WHERE id = ?;";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
    }

    private void updateFilmGenres(Film film) {
        String sqlQuery = "DELETE FROM film_genres\n" +
                "WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery, film.getId());

        sqlQuery = "INSERT INTO film_genres (film_id, genre_id)\n" +
                "VALUES (?, ?);";

        List<Genre> genres = new ArrayList<>(film.getGenres());

        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setLong(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }

    private void updateFilmLikes(Film film) {
        String sqlQuery = "DELETE FROM film_users_likes\n" +
                "WHERE film_id = ?;";

        jdbcTemplate.update(sqlQuery, film.getId());

        sqlQuery = "INSERT INTO film_users_likes (film_id, user_id)\n" +
                "VALUES (?, ?);";

        List<Long> likes = new ArrayList<>(film.getUsersLikes());

        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setLong(2, likes.get(i));
            }

            @Override
            public int getBatchSize() {
                return likes.size();
            }
        });
    }

    private List<Map<String, Object>> getGenres() {
        String sqlQuery = "SELECT fg.film_id,\n" +
                "\tg.genre\n" +
                "FROM film_genres AS fg\n" +
                "JOIN genres AS g ON fg.genre_id = g.id\n" +
                "ORDER BY g.id;";

        List<Map<String, Object>> res = new ArrayList<>(jdbcTemplate.queryForList(sqlQuery));
        return res;
    }

    private Set<Genre> getGenres(long filmId) {
        String sqlQuery = "SELECT g.genre\n" +
                "FROM film_genres AS fg\n" +
                "JOIN genres AS g ON fg.genre_id = g.id\n" +
                "WHERE film_id = ?\n" +
                "ORDER BY g.id;";

        return new LinkedHashSet<>(jdbcTemplate.query(sqlQuery, Mapper::mapRowToGenre, filmId));
    }

    private List<Map<String, Object>> getLikes() {
        String sqlQuery = "SELECT user_id,\n" +
                "\tfilm_id\n" +
                "FROM film_users_likes\n" +
                "ORDER BY film_id, user_id;";

        return new ArrayList<>(jdbcTemplate.queryForList(sqlQuery));
    }

    private Set<Long> getLikes(long filmId) {
        String sqlQuery = "SELECT user_id\n" +
                "FROM film_users_likes\n" +
                "WHERE film_id = ?\n" +
                "ORDER BY user_id;";

        return new LinkedHashSet<>(jdbcTemplate.query(sqlQuery, Mapper::mapRowToLikes, filmId));
    }
}
