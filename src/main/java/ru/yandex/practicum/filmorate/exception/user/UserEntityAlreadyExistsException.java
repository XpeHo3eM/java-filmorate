package ru.yandex.practicum.filmorate.exception.user;

public class UserEntityAlreadyExistsException extends RuntimeException {
    public UserEntityAlreadyExistsException(String message) {
        super(message);
    }
}
