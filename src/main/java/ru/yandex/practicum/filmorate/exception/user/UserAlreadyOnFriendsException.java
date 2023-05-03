package ru.yandex.practicum.filmorate.exception.user;

public class UserAlreadyOnFriendsException extends RuntimeException {
    public UserAlreadyOnFriendsException(String message) {
        super(message);
    }
}
