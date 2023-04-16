package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidator {
    public static void validate(User user) throws ValidationException {
        if (user.getEmail().isBlank()) {
            throw new ValidationException("Электронная почта не может быть пустой");
        }

        final String login = user.getLogin();
        if (login.isBlank()) {
            throw new ValidationException("Логин не может быть пустым");
        }
        if (login.contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }

        final String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }

        LocalDate now = LocalDate.now();
        if (now.isBefore(user.getBirthday())) {
            throw new ValidationException("Дата рождения не может быть позже текущей даты: "
                                            + "\nсейчас " + now + "\nдень рождения: " + user.getBirthday());
        }
    }
}
