package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

public class FilmValidator {
    private static final int       MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_RELEASE_DATE_TIME  = LocalDate.of(1895, Month.DECEMBER, 28);

    public static void validate(Film film) throws ValidationException {
        if (film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            throw new ValidationException("Максимальная длина описания " + MAX_DESCRIPTION_LENGTH + " символов");
        }

        if (MIN_RELEASE_DATE_TIME.isAfter(film.getReleaseDate())) {
            throw new ValidationException("Дата релиза фильма должна быть позже " + MIN_RELEASE_DATE_TIME);
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть больше 0");
        }
    }
}
