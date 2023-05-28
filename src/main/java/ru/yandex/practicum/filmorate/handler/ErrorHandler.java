package ru.yandex.practicum.filmorate.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.film.FilmAlreadyLikedException;
import ru.yandex.practicum.filmorate.exception.film.FilmEntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.film.FilmNotLikedException;
import ru.yandex.practicum.filmorate.exception.genre.GenreEntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.mpa.MpaEntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserAlreadyOnFriendsException;
import ru.yandex.practicum.filmorate.exception.user.UserEntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotOnFriendsException;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validateException(final ValidationException e) {
        return Map.of("Validation error", e.getMessage());
    }

    @ExceptionHandler({UserEntityNotFoundException.class,
            FilmEntityNotFoundException.class,
            GenreEntityNotFoundException.class,
            MpaEntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFoundException(final RuntimeException e) {
        return Map.of("Not found", e.getMessage());
    }

    @ExceptionHandler({UserAlreadyOnFriendsException.class,
            UserNotOnFriendsException.class,
            FilmAlreadyLikedException.class,
            FilmNotLikedException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> runtimeExceptions(final RuntimeException e) {
        return Map.of("Error", e.getMessage());
    }
}
