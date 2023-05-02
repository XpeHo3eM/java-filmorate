package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film getFilmById(long id);

    List<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);
}
