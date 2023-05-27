package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface RatingMpaStorage {
    Optional<List<Mpa>> getRatingMPAs();

    Optional<Mpa> getRatingMPA(Integer id);
}
