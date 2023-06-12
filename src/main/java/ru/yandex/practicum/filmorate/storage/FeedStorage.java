package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.util.List;

public interface FeedStorage {
    void createFeed(Long userId, Long entityId, String eventType, String operation);

    List<FeedEvent> getFeedByUserId(Long userId);
}
