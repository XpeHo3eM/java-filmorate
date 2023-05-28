package ru.yandex.practicum.filmorate.exception.film;

public class FilmEntityNotFoundException extends RuntimeException {
    public FilmEntityNotFoundException(String message) {
        super(message);
    }
}
