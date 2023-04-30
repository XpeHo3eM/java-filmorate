package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static int               id = 0;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User getUserById(long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        UserValidator.validate(user);

        user.setId(++id);
        user.setFriends(new HashSet<>());

        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User updateUser(User user) {
        UserValidator.validate(user);

        long id = user.getId();

        if (!users.containsKey(id)) {
            throw new ObjectNotFoundException(String.format("Пользователь с id = %s не найден", id));
        }

        User old = users.get(id);
        user.setFriends(old.getFriends());

        users.put(id, user);

        return user;
    }
}
