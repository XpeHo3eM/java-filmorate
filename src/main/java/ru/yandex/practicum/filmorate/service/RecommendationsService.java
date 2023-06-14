package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface RecommendationsService {
    List<Film> getRecommended(long userId);
}
