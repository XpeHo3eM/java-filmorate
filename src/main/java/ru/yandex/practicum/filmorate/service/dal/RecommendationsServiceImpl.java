package ru.yandex.practicum.filmorate.service.dal;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.RecommendationsService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

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
