package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    List<User> addFriend(Long fromId, Long toId);

    List<User> removeFriend(Long fromId, Long toId);

    List<User> getMutualFriends(Long fromId, Long toId);

    User getUserById(Long id);

    List<User> getAllUsers();

    User addUser(User user);

    User updateUser(User user);

    List<User> getFriends(Long id);

    void removeUserById(long userId);
}
