package ru.yandex.practicum.filmorate.service.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.entity.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final FeedStorage feedStorage;
    private final UserStorage userStorage;

    @Override
    public void createFeed(Long userId, Long entityId, String eventType, String operation) {
        feedStorage.createFeed(userId, entityId, eventType, operation);
    }

    @Override
    public List<FeedEvent> getFeedByUserId(Long userId) {
        User user = userStorage.getUserById(userId);

        if (user != null) {
            return feedStorage.getFeedByUserId(userId);
        }

        throw new EntityNotFoundException(String.format("Пользователь с ID = %s не найден", userId));
    }
}