package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static int               id    = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.debug(film.toString());

        FilmValidator.validate(film);

        if (films.containsValue(film)) {
            throwError("Текущий фильм уже добавлен: " + film);
        }

        film.setId(++id);
        films.put(id, film);

        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug(film.toString());

        FilmValidator.validate(film);

        int id = film.getId();
        if (!films.containsKey(id)) {
            throwError("Отсутствует фильм с таким ID: " + film.getId());
        }

        films.put(id, film);

        return film;
    }

    private void throwError (final String msg) throws ValidationException {
        ValidationException e = new ValidationException(msg);
        log.warn(e.getMessage());
        throw e;
    }
}
