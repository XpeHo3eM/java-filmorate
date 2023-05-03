package ru.yandex.practicum.filmorate.exception.film;

public class FilmNotLikedException extends RuntimeException {
    public FilmNotLikedException(String message) {
        super(message);
    }
}
