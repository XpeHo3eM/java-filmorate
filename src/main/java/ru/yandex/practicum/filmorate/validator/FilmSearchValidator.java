package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilmSearchValidator {
    private static final Set<String> availableSearchBy = Stream.of("director", "title")
            .collect(Collectors.toUnmodifiableSet());

    public static void validate(List<String> searchBy) {
        if (searchBy.isEmpty()) {
            throw new ValidationException("Не указан критерий поиска");
        }

        if (!availableSearchBy.containsAll(searchBy)) {
            throw new ValidationException("Некорректные данные для поиска");
        }
    }
}
