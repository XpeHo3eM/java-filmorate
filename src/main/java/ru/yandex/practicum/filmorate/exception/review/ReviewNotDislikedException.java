package ru.yandex.practicum.filmorate.exception.review;

public class ReviewNotDislikedException extends RuntimeException {
    public ReviewNotDislikedException(String message) {
        super(message);
    }
}
