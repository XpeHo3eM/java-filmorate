package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    void addFriend(long user1Id, long user2Id);

    void removeFriend(long user1Id, long user2Id);

    Set<User> getMutualFriends(long user1Id, long user2Id);

    User getUserById(long id);

    List<User> getAllUsers();

    User addUser(User user);

    User updateUser(User user);

    List<User> getFriends(long id);
}
