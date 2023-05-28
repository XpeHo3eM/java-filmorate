package ru.yandex.practicum.filmorate.exception.genre;

public class GenreEntityNotFoundException extends RuntimeException {
    public GenreEntityNotFoundException(String message) {
        super(message);
    }
}
