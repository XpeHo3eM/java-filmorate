package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.PastOrPresent;

@Data
@Builder
public class FeedEvent {
    @PastOrPresent
    @NonNull
    private Long timestamp;
    @NonNull
    private Long userId;
    @NonNull
    private String eventType;
    @NonNull
    private String operation;
    @NonNull
    private Long eventId;
    @NonNull
    private Long entityId;
}
