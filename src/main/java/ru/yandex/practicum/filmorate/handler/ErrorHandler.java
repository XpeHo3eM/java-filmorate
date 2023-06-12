package ru.yandex.practicum.filmorate.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.entity.*;
import ru.yandex.practicum.filmorate.exception.review.*;
import ru.yandex.practicum.filmorate.exception.film.*;
import ru.yandex.practicum.filmorate.exception.user.*;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validateException(final ValidationException e) {
        return Map.of("Validation error", e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFoundException(final RuntimeException e) {
        return Map.of("Not found", e.getMessage());
    }

    @ExceptionHandler({UserAlreadyOnFriendsException.class,
            UserNotOnFriendsException.class,
            FilmAlreadyLikedException.class,
            FilmNotLikedException.class,
            EntityAlreadyExistsException.class,
            EntityNotDeletedException.class,
            ReviewNotCreatedException.class,
            ReviewNotDislikedException.class,
            ReviewNotLikedException.class,
            ReviewNotRemovedDislikeException.class,
            ReviewNotRemovedException.class,
            ReviewNotRemovedLikeException.class,
            ReviewNotUpdatedException.class
    })

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> runtimeExceptions(final RuntimeException e) {
        return Map.of("Error", e.getMessage());
    }
}
