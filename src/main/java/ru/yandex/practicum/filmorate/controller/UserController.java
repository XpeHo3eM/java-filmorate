package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private static int               id    = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.debug(user.toString());

        UserValidator.validate(user);

        user.setId(++id);
        users.put(user.getId(), user);

        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug(user.toString());

        UserValidator.validate(user);

        int id = user.getId();
        if (!users.containsKey(id)) {
            throwError("Пользователь с таким ID: " + user.getId() + " не найден");
        }

        users.put(id, user);

        return user;
    }

    private void throwError (final String msg) throws ValidationException {
        ValidationException e = new ValidationException(msg );
        log.warn(e.getMessage());
        throw e;
    }
}
