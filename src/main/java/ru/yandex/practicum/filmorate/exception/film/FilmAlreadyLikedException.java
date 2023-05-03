package ru.yandex.practicum.filmorate.exception.film;

public class FilmAlreadyLikedException extends RuntimeException {
    public FilmAlreadyLikedException(String message) {
        super(message);
    }
}
