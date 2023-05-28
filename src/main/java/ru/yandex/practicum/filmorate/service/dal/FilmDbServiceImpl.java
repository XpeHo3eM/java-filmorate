package ru.yandex.practicum.filmorate.service.dal;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.film.FilmAlreadyLikedException;
import ru.yandex.practicum.filmorate.exception.film.FilmEntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.film.FilmEntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.film.FilmNotLikedException;
import ru.yandex.practicum.filmorate.exception.user.UserEntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmDbServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmDbServiceImpl(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        User user = getUserOrThrowException(userId);
        Film film = getFilmOrThrowException(filmId);
        Set<Long> likes = film.getUsersLikes();

        if (likes.contains(userId)) {
            throw new FilmAlreadyLikedException(String.format("%s уже лайкал \"%s\"", user.getName(), film.getName()));
        }

        likes.add(userId);
        filmStorage.updateFilm(film);

        return film;
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        User user = getUserOrThrowException(userId);
        Film film = getFilmOrThrowException(filmId);
        Set<Long> likes = film.getUsersLikes();

        if (!likes.contains(userId)) {
            throw new FilmNotLikedException(String.format("%s еще не лайкал \"%s\"", user.getName(), film.getName()));
        }

        likes.remove(userId);
        filmStorage.updateFilm(film);

        return film;
    }

    @Override
    public List<Film> getPopulated(Integer filmsCount) {
        final int DEFAULT_FILMS_COUNT = 10;
        int maxFilms = filmsCount != null ? filmsCount : DEFAULT_FILMS_COUNT;
        final List<Film> films = filmStorage.getAllFilms();

        return films.stream().sorted((f1, f2) -> {
            return f2.getUsersLikes().size() - f1.getUsersLikes().size();
        }).limit(maxFilms).collect(Collectors.toList());
    }

    @Override
    public Film getFilmById(Long id) {
        return getFilmOrThrowException(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film addFilm(Film film) {
        FilmValidator.validate(film);

        Film filmOnDb = filmStorage.addFilm(film);

        if (filmOnDb == null) {
            throw new FilmEntityAlreadyExistsException(String.format("Фильм с ID = %s уже добавлен", film.getId()));
        }

        return filmOnDb;
    }

    @Override
    public Film updateFilm(Film film) {
        FilmValidator.validate(film);

        Film filmOnDb = filmStorage.updateFilm(film);

        if (filmOnDb == null) {
            throw new FilmEntityNotFoundException(String.format("Фильм с ID = %s не найден", film.getId()));
        }

        return filmOnDb;
    }

    private User getUserOrThrowException(Long id) {
        User userOnDb = userStorage.getUserById(id);

        if (userOnDb == null) {
            throw new UserEntityNotFoundException(String.format("Пользователь с ID = %s не найден", id));
        }
        return userOnDb;
    }

    private Film getFilmOrThrowException(Long id) {
        Film filmOnDb = filmStorage.getFilmById(id);

        if (filmOnDb == null) {
            throw new FilmEntityNotFoundException(String.format("Фильм с ID = %s не найден", id));
        }

        return filmOnDb;
    }
}
