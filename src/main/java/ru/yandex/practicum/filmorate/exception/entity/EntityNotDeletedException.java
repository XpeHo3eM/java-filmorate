package ru.yandex.practicum.filmorate.exception.entity;

public class EntityNotDeletedException extends RuntimeException {
    public EntityNotDeletedException(String message) {
        super(message);
    }
}
