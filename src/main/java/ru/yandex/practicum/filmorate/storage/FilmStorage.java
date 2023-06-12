package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film getFilmById(long id);

    List<Film> getAllFilms();

    List<Film> getAllFilmsByDirector(long directorId);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> searchFilm(String query, List<String> searchBy);

    List<Film> getRecommendations(Long forUserId, Long fromUserId);

    List<Film> commonAndPopularFilm(long userId, long friendId);

    int removeFilm(long id);
}
