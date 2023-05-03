package ru.yandex.practicum.filmorate.exception.user;

public class UserNotOnFriendsException extends RuntimeException {
    public UserNotOnFriendsException(String message) {
        super(message);
    }
}
