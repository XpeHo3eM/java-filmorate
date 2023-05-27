package ru.yandex.practicum.filmorate.service.dal;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
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

        return storage.addUser(correctUser(user)).orElseThrow(() ->
                new ObjectAlreadyExistsException(String.format("Пользователь с ID = %s уже добавлен", user.getId())));
    }

    @Override
    public User updateUser(User user) {
        UserValidator.validate(user);

        return storage.updateUser(correctUser(user)).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Пользователь с ID = %s не найден", user.getId())));
    }

    @Override
    public List<User> getFriends(Long id) {
        getUserOrThrowException(id);

        return storage.getFriends(id).orElse(new ArrayList<>());
    }

    @Override
    public List<User> addFriend(Long fromId, Long toId) {
        getUserOrThrowException(fromId);
        getUserOrThrowException(toId);

        return storage.addFriend(fromId, toId).orElse(new ArrayList<>());
    }

    @Override
    public List<User> removeFriend(Long fromId, Long toId) {
        getUserOrThrowException(fromId);
        getUserOrThrowException(toId);

        return storage.removeFriend(fromId, toId).orElse(new ArrayList<>());
    }

    @Override
    public List<User> getMutualFriends(Long fromId, Long toId) {
        getUserOrThrowException(fromId);
        getUserOrThrowException(toId);

        List<User> from = storage.getFriends(fromId).orElse(new ArrayList<>());
        List<User> to = storage.getFriends(toId).orElse(new ArrayList<>());

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
        return storage.getAllUsers().orElse(new ArrayList<>());
    }

    private User getUserOrThrowException(Long id) {
        return storage.getUserById(id).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Пользователь с ID = %s не найден", id)));
    }

    private User correctUser(User user) {
        String name = user.getName();

        if (name == null || name.isEmpty()) {
            user.setName(user.getLogin());
        }

        return user;
    }
}
