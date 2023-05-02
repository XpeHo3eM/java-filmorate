package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.user.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static long id = 0;
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

        if (users.containsKey(user.getId())) {
            throw new UserAlreadyExistsException(String.format("Пользователь с ID = %s уже добавлен", user.getId()));
        }

        user.setId(++id);
        user.setFriendsIds(new HashSet<>());

        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User updateUser(User user) {
        UserValidator.validate(user);

        Long id = user.getId();

        if (id == null) {
            throw new ValidationException("Отсутствует ID пользователя для поиска");
        }

        if (!users.containsKey(id)) {
            throw new ObjectNotFoundException(String.format("Пользователь с ID = %s не найден", id));
        }

        User old = users.get(id);
        user.setFriendsIds(old.getFriendsIds());

        users.put(id, user);

        return user;
    }
}
