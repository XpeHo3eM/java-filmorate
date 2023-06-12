package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Review {
    private final long reviewId;

    @NotBlank(message = "content can't be blank")
    private final String content;

    @NonNull
    private final Boolean isPositive;

    @NonNull
    private final Long userId;

    @NonNull
    private final Long filmId;

    private final long useful;
}
