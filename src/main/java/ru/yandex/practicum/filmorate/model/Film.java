package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class Film {
    @EqualsAndHashCode.Exclude
    private long id;

    @NotBlank(message = "name can't be blank")
    private String name;

    @Length(max = 200, message = "description length can't be greater than 200 chars")
    private String description;

    @PastOrPresent(message = "release date can't be on future")
    private LocalDate releaseDate;

    @Positive
    private int duration;

    private Set<Long> usersLikes;
}
