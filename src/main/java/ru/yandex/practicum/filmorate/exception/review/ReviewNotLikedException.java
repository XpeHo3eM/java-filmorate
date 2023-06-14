package ru.yandex.practicum.filmorate.exception.review;

public class ReviewNotLikedException extends RuntimeException {
    public ReviewNotLikedException(String message) {
        super(message);
    }
}
