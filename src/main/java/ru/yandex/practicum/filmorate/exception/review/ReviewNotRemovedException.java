package ru.yandex.practicum.filmorate.exception.review;

public class ReviewNotRemovedException extends RuntimeException {
    public ReviewNotRemovedException(String message) {
        super(message);
    }
}
