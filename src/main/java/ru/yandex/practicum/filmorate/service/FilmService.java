package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film addLike(Long filmId, Long userId);

    Film removeLike(Long filmId, Long userId);

    List<Film> getPopulated(Integer filmsCount);

    Film getFilmById(Long id);

    List<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);
}
