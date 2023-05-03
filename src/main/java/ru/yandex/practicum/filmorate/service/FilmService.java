package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    List<Film> getPopulated(Integer filmsCount);

    Film getFilmById(long id);

    List<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);
}
