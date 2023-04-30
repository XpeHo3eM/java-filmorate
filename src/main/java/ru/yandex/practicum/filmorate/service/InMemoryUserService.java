package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserAlreadyOnFriendsException;
import ru.yandex.practicum.filmorate.exception.user.UserNotOnFriendsException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class InMemoryUserService implements UserService {
    private final UserStorage storage;

    @Autowired
    public InMemoryUserService(UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public void addFriend(long user1Id, long user2Id) {
        User      user1    = getUserOrThrowException(user1Id);
        User      user2    = getUserOrThrowException(user2Id);
        Set<Long> friends1 = user1.getFriends();
        Set<Long> friends2 = user2.getFriends();

        if (friends1.contains(user2Id)) {
            throw new UserAlreadyOnFriendsException(String.format("%s и $s уже друзья.", user1.getName(), user2.getName()));
        }

        friends1.add(user2Id);
        friends2.add(user1Id);
    }

    @Override
    public void removeFriend(long user1Id, long user2Id) {
        User      user1    = getUserOrThrowException(user1Id);
        User      user2    = getUserOrThrowException(user2Id);
        Set<Long> friends1 = user1.getFriends();
        Set<Long> friends2 = user2.getFriends();

        if (!friends1.contains(user2Id)) {
            throw new UserNotOnFriendsException(String.format("%s и $s не друзья.", user1.getName(), user2.getName()));
        }

        friends1.remove(user2Id);
        friends2.remove(user1Id);
    }

    @Override
    public Set<User> getMutualFriends(long user1Id, long user2Id) {
        User      user1        = getUserOrThrowException(user1Id);
        User      user2        = getUserOrThrowException(user2Id);
        Set<Long> intersection = new HashSet<>(user1.getFriends());

        intersection.retainAll(user2.getFriends());

        Set<User> friendsIntersection = new HashSet<>();

        intersection.forEach(id -> friendsIntersection.add(storage.getUserById(id)));

        return friendsIntersection;
    }

    @Override
    public User getUserById(long id) {
        return getUserOrThrowException(id);
    }

    @Override
    public List<User> getAllUsers() {
        return storage.getAllUsers();
    }

    @Override
    public User addUser(User user) {
        return storage.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        return storage.updateUser(user);
    }

    @Override
    public List<User> getFriends(long id) {
        Set<Long> friendsIds = storage.getUserById(id).getFriends();
        List<User> friends    = new ArrayList<>();

        friendsIds.forEach(userId -> friends.add(storage.getUserById(userId)));

        return friends;
    }

    private User getUserOrThrowException(long userId) {
        User user = storage.getUserById(userId);

        if (user == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id = %s не найден", userId));
        }

        return user;
    }
}
