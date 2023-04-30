package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.film.FilmAlreadyLikedException;
import ru.yandex.practicum.filmorate.exception.film.FilmNotLikedException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InMemoryFilmService implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public void addLike(long filmId, long userId) {
        User      user  = getUserOrThrowException(userId);
        Film      film  = getFilmOrThrowException(filmId);
        Set<Long> likes = film.getUsersLikes();

        if (likes.contains(userId)) {
            throw new FilmAlreadyLikedException(String.format("%s уже лайкал \"%s\"", user.getName(), film.getName()));
        }

        likes.add(userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        User      user  = getUserOrThrowException(userId);
        Film      film  = getFilmOrThrowException(filmId);
        Set<Long> likes = film.getUsersLikes();

        if (!likes.contains(userId)) {
            throw new FilmNotLikedException(String.format("%s еще не лайкал \"%s\"", user.getName(), film.getName()));
        }

        likes.remove(userId);
    }

    @Override
    public List<Film> getPopulated(Integer filmsCount) {
        final long       DEFAULT_FILMS_COUNT = 10;
        long             maxFilms            = filmsCount != null ? filmsCount : DEFAULT_FILMS_COUNT;
        final List<Film> films               = filmStorage.getAllFilms();

        return films.stream()
                .sorted((f1, f2) -> {
                    return f2.getUsersLikes().size() - f1.getUsersLikes().size(); })
                .limit(maxFilms)
                .collect(Collectors.toList());
    }

    @Override
    public Film getFilmById(long id) {
        return getFilmOrThrowException(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    private User getUserOrThrowException(long id) {
        User user = userStorage.getUserById(id);

        if (user == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id = %s не найден", id));
        }

        return user;
    }

    private Film getFilmOrThrowException(long id) {
        Film film = filmStorage.getFilmById(id);

        if (film == null) {
            throw new ObjectNotFoundException(String.format("Фильм с id = %s не найден", id));
        }

        return film;
    }
}
