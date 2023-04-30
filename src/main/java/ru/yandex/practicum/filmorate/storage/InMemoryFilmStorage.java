package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.film.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static long              id = 0;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film getFilmById(long id) {
        return films.get(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        FilmValidator.validate(film);

        if (films.containsValue(film)) {
            throw new FilmAlreadyExistsException(String.format("Текущий фильм уже добавлен: %s", film));
        }

        film.setId(++id);
        film.setUsersLikes(new HashSet<>());
        films.put(id, film);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        FilmValidator.validate(film);

        long id = film.getId();

        if (!films.containsKey(id)) {
            throw new ObjectNotFoundException(String.format("Фильм с id = %s не найден", id));
        }

        Film old = films.get(id);
        film.setUsersLikes(old.getUsersLikes());

        films.put(id, film);

        return film;
    }
}
