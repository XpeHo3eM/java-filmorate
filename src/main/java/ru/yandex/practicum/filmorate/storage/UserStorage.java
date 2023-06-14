package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User getUserById(long id);

    List<User> getAllUsers();

    User addUser(User user);

    User updateUser(User user);

    List<User> getFriends(Long id);

    List<User> addFriend(Long fromId, Long toId);

    List<User> removeFriend(Long fromId, Long toId);

    Long getLikesCount(Long id);

    Long getUserIdWithMostCommonLikes(Long id);

    int removeUser(long userId);
}
