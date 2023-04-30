package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmService {
    public void addLike(long filmId, long userId);
    public void removeLike(long filmId, long userId);
    public List<Film> getPopulated(Integer filmsCount);
    public Film getFilmById(long id);
    public List<Film> getAllFilms();
    public Film addFilm(Film film);
    public Film updateFilm(Film film);
}
