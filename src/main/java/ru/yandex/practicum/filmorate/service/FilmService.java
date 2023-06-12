package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film addLike(Long filmId, Long userId);

    Film removeLike(Long filmId, Long userId);

    List<Film> getPopulated(Integer filmsCount, Integer genreId, Integer year);

    Film getFilmById(Long id);

    List<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getDirectorFilms(Long directorId, String sortBy);

    List<Film> commonAndPopularFilm(Long userId, Long friendId);

    List<Film> searchFilm(String query, List<String> searchBy);

    void removeFilmById(long id);
}
