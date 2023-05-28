package ru.yandex.practicum.filmorate.exception.user;

public class UserEntityNotFoundException extends RuntimeException {
    public UserEntityNotFoundException(String message) {
        super(message);
    }
}
