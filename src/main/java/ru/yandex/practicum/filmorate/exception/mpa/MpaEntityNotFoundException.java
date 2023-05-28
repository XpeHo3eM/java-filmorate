package ru.yandex.practicum.filmorate.exception.mpa;

public class MpaEntityNotFoundException extends RuntimeException {
    public MpaEntityNotFoundException(String message) {
        super(message);
    }
}
