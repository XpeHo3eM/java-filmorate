package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
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
    private final DirectorStorage directorStorage;

    public FilmDao(JdbcTemplate jdbcTemplate, DirectorStorage directorStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.directorStorage = directorStorage;
    }

    @Override
    @Transactional
    public Film getFilmById(long id) {
        String sqlQuery = "SELECT f.id,\n" +
                "\tf.name,\n" +
                "\tf.description,\n" +
                "\tf.release_date,\n" +
                "\tf.duration,\n" +
                "\tr.rating\n" +
                "FROM films AS f\n" +
                "JOIN mpas AS r ON f.rating_id = r.id\n" +
                "WHERE f.id = ?;";

        Film film;

        try {
            film = jdbcTemplate.queryForObject(sqlQuery, Mapper::mapRowToFilm, id);
        } catch (DataAccessException e) {
            return null;
        }

        film.setGenres(getFilmGenres(id));
        film.setUsersLikes(getFilmLikes(id));
        film.setDirectors(getFilmDirectors(id));

        return film;
    }

    @Override
    @Transactional
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT f.id,\n" +
                "\tf.name,\n" +
                "\tf.description,\n" +
                "\tf.release_date,\n" +
                "\tf.duration,\n" +
                "\tr.rating\n" +
                "FROM films AS f\n" +
                "JOIN mpas AS r ON f.rating_id = r.id\n" +
                "ORDER BY f.id;";

        List<Film> films = jdbcTemplate.query(sqlQuery, Mapper::mapRowToFilm);

        fillFilmsInfo(films);

        return films;
    }

    @Override
    @Transactional
    public List<Film> getAllFilmsByDirector(long directorId) {
        String sqlQuery = "SELECT f.id,\n" +
                "\tf.name,\n" +
                "\tf.description,\n" +
                "\tf.release_date,\n" +
                "\tf.duration,\n" +
                "\tr.rating\n" +
                "FROM films AS f\n" +
                "JOIN mpas AS r ON f.rating_id = r.id\n" +
                "JOIN film_directors AS fd ON f.id = fd.film_id\n" +
                "WHERE fd.director_id = ?\n" +
                "ORDER BY f.id;";

        List<Film> films = jdbcTemplate.query(sqlQuery, Mapper::mapRowToFilm, directorId);

        fillFilmsInfo(films);

        return films;
    }

    @Override
    @Transactional
    public Film addFilm(Film film) {
        long filmId = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(Mapper.filmToMap(film))
                .longValue();

        film.setId(filmId);

        addFilmGenres(film);
        addFilmUsersLikes(film);
        addFilmDirectors(film);

        return film;
    }

    @Override
    @Transactional
    public Film updateFilm(Film film) {
        if (getFilmById(film.getId()) == null) {
            return null;
        }

        try {
            updateFilmsTable(film);
        } catch (DataAccessException e) {
            return null;
        }

        updateFilmGenres(film);
        updateFilmLikes(film);
        updateFilmDirectors(film);

        return film;
    }


    @Override
    public List<Film> getRecommendations(Long forUserId, Long fromUserId) {
        String sqlQuery = "SELECT f.id,\n" +
                "       \tf.name,\n" +
                "       \tf.description,\n" +
                "       \tf.release_date,\n" +
                "       \tf.duration,\n" +
                "       \tr.rating\n" +
                "FROM films AS f\n" +
                "JOIN mpas AS r ON f.rating_id = r.id\n" +
                "LEFT JOIN film_users_likes AS u1 ON f.id = u1.film_id AND u1.user_id = ?\n" +
                "LEFT JOIN film_users_likes AS u2 ON f.id = u2.film_id AND u2.user_id = ?\n" +
                "WHERE u1.film_id IS NULL AND u2.film_id IS NOT NULL\n" +
                "ORDER BY f.id;\n";

        List<Film> films = jdbcTemplate.query(sqlQuery, Mapper::mapRowToFilm, forUserId, fromUserId);

        fillFilmsInfo(films);

        return films;
    }

    @Override
    @Transactional
    public List<Film> commonAndPopularFilm(long userId, long friendId) {
        String sqlQuery = "SELECT f.id,\n" +
                "\tf.name,\n" +
                "\tf.description,\n" +
                "\tf.release_date,\n" +
                "\tf.duration,\n" +
                "\tr.rating\n" +
                "FROM film_users_likes AS fl\n" +
                "LEFT JOIN films AS f ON fl.film_id = f.id\n" +
                "LEFT JOIN mpas AS r ON f.rating_id = r.id\n" +
                "WHERE fl.user_id IN (?,?)\n" +
                "GROUP BY fl.film_id\n" +
                "HAVING (COUNT(fl.film_id) > 1)\n" +
                "ORDER BY f.id;";

        List<Film> films = jdbcTemplate.query(sqlQuery, Mapper::mapRowToFilm, userId, friendId);
        fillFilmsInfo(films);

        return films;
    }

    @Override
    @Transactional
    public int removeFilm(long filmId) {
        String sqlQuery = "DELETE\n" +
                "FROM films\n" +
                "WHERE id = ?;";

        return jdbcTemplate.update(sqlQuery, filmId);
    }

    private void fillFilmsInfo(List<Film> films) {
        Map<Long, Film> filmsMap = films.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));

        Map<String, Genre> genresEntity = GenreDao.getGenreNameToGenreMap();

        getFilmGenres().forEach(row -> {
            Film film = filmsMap.get(Long.parseLong(row.get("film_id").toString()));

            if (film != null) {
                film.getGenres().add(genresEntity.get(row.get("genre").toString()));
            }
        });
        getFilmLikes().forEach(row -> {
            Film film = filmsMap.get(Long.parseLong(row.get("film_id").toString()));

            if (film != null) {
                film.getUsersLikes().add(Long.parseLong(row.get("user_id").toString()));
            }
        });
        getFilmDirectors().forEach(row -> {
            Film film = filmsMap.get(Long.parseLong(row.get("film_id").toString()));

            if (film != null) {
                film.getDirectors().add(directorStorage.getDirectorById(Long.parseLong(row.get("director_id").toString())));
            }
        });
    }

    @Override
    @Transactional
    public List<Film> searchFilm(String query, List<String> searchBy) {
        Set<String> searchBySet = new HashSet<>(searchBy);
        query = "%" + query + "%";

        List<Film> films;
        String sqlQuery = "";

        boolean searchByDirector = searchBySet.contains("director");
        boolean searchByTitle = searchBySet.contains("title");

        if (searchByDirector && searchByTitle) {
            sqlQuery = getSqlQuerySearchByDirectorAndTitle();
        } else if (searchByDirector) {
            sqlQuery = getSqlQuerySearchByDirector();
        } else if (searchByTitle) {
            sqlQuery = getSqlQuerySearchByTitle();
        }

        if (searchByDirector && searchByTitle) {
            films = jdbcTemplate.query(sqlQuery, Mapper::mapRowToFilm, query, query);
        } else {
            films = jdbcTemplate.query(sqlQuery, Mapper::mapRowToFilm, query);
        }

        fillFilmsInfo(films);

        return films;
    }

    private String getSqlQuerySearchByTitle() {
        return "SELECT f.id,\n" +
                "\tf.name,\n" +
                "\tf.description,\n" +
                "\tf.release_date,\n" +
                "\tf.duration,\n" +
                "\tr.rating\n" +
                "FROM films AS f\n" +
                "JOIN mpas AS r ON f.rating_id = r.id\n" +
                "WHERE UPPER(f.name) LIKE UPPER(?)\n" +
                "ORDER BY f.id;";
    }

    private String getSqlQuerySearchByDirector() {
        return "SELECT f.id,\n" +
                "\tf.name,\n" +
                "\tf.description,\n" +
                "\tf.release_date,\n" +
                "\tf.duration,\n" +
                "\tr.rating\n" +
                "FROM films AS f\n" +
                "JOIN mpas AS r ON f.rating_id = r.id\n" +
                "WHERE f.id IN (SELECT fd.film_id\n" +
                "\tFROM film_directors AS fd\n" +
                "\tJOIN directors AS d ON d.id = fd.director_id\n" +
                "\tWHERE UPPER(d.name) LIKE UPPER(?))\n" +
                "ORDER BY f.id;";
    }

    private String getSqlQuerySearchByDirectorAndTitle() {
        return "SELECT f.id,\n" +
                "\tf.name,\n" +
                "\tf.description,\n" +
                "\tf.release_date,\n" +
                "\tf.duration,\n" +
                "\tr.rating\n" +
                "FROM films AS f\n" +
                "JOIN mpas AS r ON f.rating_id = r.id\n" +
                "WHERE f.id IN (SELECT fd.film_id\n" +
                "\tFROM film_directors AS fd\n" +
                "\tJOIN directors AS d ON d.id = fd.director_id\n" +
                "\tWHERE UPPER(d.name) LIKE UPPER(?)) OR UPPER(f.name) LIKE UPPER(?)\n" +
                "ORDER BY f.id;";
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

    private void addFilmDirectors(Film film) {
        String sqlQuery = "INSERT INTO film_directors (film_id, director_id)\n" +
                "VALUES(?, ?);";

        List<Director> directors = new ArrayList<>(film.getDirectors());

        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setLong(2, directors.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return directors.size();
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

    private void updateFilmDirectors(Film film) {
        String sqlQuery = "DELETE FROM film_directors\n" +
                "WHERE film_id = ?;";

        jdbcTemplate.update(sqlQuery, film.getId());

        addFilmDirectors(film);
    }

    private List<Map<String, Object>> getFilmGenres() {
        String sqlQuery = "SELECT fg.film_id,\n" +
                "\tg.genre\n" +
                "FROM film_genres AS fg\n" +
                "JOIN genres AS g ON fg.genre_id = g.id\n" +
                "ORDER BY g.id;";

        return new ArrayList<>(jdbcTemplate.queryForList(sqlQuery));
    }

    private Set<Genre> getFilmGenres(long filmId) {
        String sqlQuery = "SELECT g.id,\n" +
                "\tg.genre\n" +
                "FROM film_genres AS fg\n" +
                "JOIN genres AS g ON fg.genre_id = g.id\n" +
                "WHERE film_id = ?\n" +
                "ORDER BY g.id;";

        return new LinkedHashSet<>(jdbcTemplate.query(sqlQuery, Mapper::mapRowToGenre, filmId));
    }

    private List<Map<String, Object>> getFilmLikes() {
        String sqlQuery = "SELECT user_id,\n" +
                "\tfilm_id\n" +
                "FROM film_users_likes\n" +
                "ORDER BY film_id, user_id;";

        return new ArrayList<>(jdbcTemplate.queryForList(sqlQuery));
    }

    private Set<Long> getFilmLikes(long filmId) {
        String sqlQuery = "SELECT user_id\n" +
                "FROM film_users_likes\n" +
                "WHERE film_id = ?\n" +
                "ORDER BY user_id;";

        return new LinkedHashSet<>(jdbcTemplate.query(sqlQuery, Mapper::mapRowToLikes, filmId));
    }

    private List<Map<String, Object>> getFilmDirectors() {
        String sqlQuery = "SELECT director_id,\n" +
                "\tfilm_id\n" +
                "FROM film_directors\n" +
                "ORDER BY director_id, film_id;";

        return new ArrayList<>(jdbcTemplate.queryForList(sqlQuery));
    }

    private Set<Director> getFilmDirectors(long filmId) {
        String sqlQuery = "SELECT d.id,\n" +
                "\td.name\n" +
                "FROM film_directors AS fd\n" +
                "JOIN directors AS d ON d.id = fd.director_id\n" +
                "WHERE fd.film_id = ?\n" +
                "ORDER BY d.id;";

        return new LinkedHashSet<>(jdbcTemplate.query(sqlQuery, Mapper::mapRowToDirector, filmId));
    }
}
