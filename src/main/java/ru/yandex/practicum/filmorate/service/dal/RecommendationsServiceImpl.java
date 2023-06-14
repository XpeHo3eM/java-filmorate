package ru.yandex.practicum.filmorate.service.dal;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.entity.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.RecommendationsService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecommendationsServiceImpl implements RecommendationsService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public RecommendationsServiceImpl(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public List<Film> getRecommended(long userId) {
        User user = Optional.ofNullable(userStorage.getUserById(userId))
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с ID = %s не найден", userId)));

        Long likesCount = userStorage.getLikesCount(userId);
        if (likesCount == null || likesCount == 0) {
            return new ArrayList<>();
        }

        Long foundId = userStorage.getUserIdWithMostCommonLikes(userId);
        if (foundId == null) {
            return new ArrayList<>();
        }

        return filmStorage.getRecommendations(userId, foundId);
    }
}
