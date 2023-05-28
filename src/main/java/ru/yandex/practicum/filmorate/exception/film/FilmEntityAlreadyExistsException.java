package ru.yandex.practicum.filmorate.exception.film;

public class FilmEntityAlreadyExistsException extends RuntimeException {
    public FilmEntityAlreadyExistsException(String message) {
        super(message);
    }
}
