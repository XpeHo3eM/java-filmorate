package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface RatingMpaService {
    List<Mpa> getRatingMPAs();

    Mpa getRatingMPA(Integer id);
}
