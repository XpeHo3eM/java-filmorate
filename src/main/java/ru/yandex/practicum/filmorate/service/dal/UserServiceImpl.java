package ru.yandex.practicum.filmorate.service.dal;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.user.UserEntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.user.UserEntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage storage;

    public UserServiceImpl(UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public User addUser(User user) {
        UserValidator.validate(user);

        User userOnDb = storage.addUser(correctUser(user));

        if (userOnDb == null) {
            throw new UserEntityAlreadyExistsException(String.format("Пользователь с ID = %s уже добавлен", user.getId()));
        }

        return userOnDb;
    }

    @Override
    public User updateUser(User user) {
        UserValidator.validate(user);

        User userOnDb = storage.updateUser(correctUser(user));

        if (userOnDb == null) {
            throw new UserEntityNotFoundException(String.format("Пользователь с ID = %s не найден", user.getId()));
        }

        return userOnDb;
    }

    @Override
    public List<User> getFriends(Long id) {
        getUserOrThrowException(id);

        return storage.getFriends(id);
    }

    @Override
    public List<User> addFriend(Long fromId, Long toId) {
        getUserOrThrowException(fromId);
        getUserOrThrowException(toId);

        return storage.addFriend(fromId, toId);
    }

    @Override
    public List<User> removeFriend(Long fromId, Long toId) {
        getUserOrThrowException(fromId);
        getUserOrThrowException(toId);

        return storage.removeFriend(fromId, toId);
    }

    @Override
    public List<User> getMutualFriends(Long fromId, Long toId) {
        getUserOrThrowException(fromId);
        getUserOrThrowException(toId);

        List<User> from = storage.getFriends(fromId);
        List<User> to = storage.getFriends(toId);

        if (from.isEmpty() || to.isEmpty()) {
            return new ArrayList<>();
        }

        from.retainAll(to);

        return from.stream()
                .sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public User getUserById(Long id) {
        return getUserOrThrowException(id);
    }

    @Override
    public List<User> getAllUsers() {
        return storage.getAllUsers();
    }

    private User getUserOrThrowException(Long id) {
        User userOnDb = storage.getUserById(id);

        if (userOnDb == null) {
            throw new UserEntityNotFoundException(String.format("Пользователь с ID = %s не найден", id));
        }

        return userOnDb;
    }

    private User correctUser(User user) {
        String name = user.getName();

        if (name == null || name.isEmpty()) {
            user.setName(user.getLogin());
        }

        return user;
    }
}
