package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> getUserById(long id);

    Optional<List<User>> getAllUsers();

    Optional<User> addUser(User user);

    Optional<User> updateUser(User user);

    Optional<List<User>> getFriends(Long id);

    Optional<List<User>> addFriend(Long fromId, Long toId);

    Optional<List<User>> removeFriend(Long fromId, Long toId);
}
