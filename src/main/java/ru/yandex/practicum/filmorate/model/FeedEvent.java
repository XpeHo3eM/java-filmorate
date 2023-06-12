package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.PastOrPresent;

@Data
@Builder
public class FeedEvent {
    @PastOrPresent
    private Long timestamp;

    private Long userId;

    private String eventType;

    private String operation;

    private Long eventId;

    private Long entityId;
}
