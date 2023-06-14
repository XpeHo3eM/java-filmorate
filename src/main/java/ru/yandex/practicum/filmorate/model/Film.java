package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class Film {
    @EqualsAndHashCode.Exclude
    private long id;

    @NotBlank(message = "name can't be blank")
    private String name;

    @Length(max = 200, message = "description length can't be greater than 200 chars")
    private String description;

    private LocalDate releaseDate;

    @Positive
    private int duration;

    private Mpa mpa;

    private Set<Long> usersLikes;
    private Set<Genre> genres;
    private Set<Director> directors;

    public Film(long id, String name, String description, LocalDate releaseDate, int duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        usersLikes = new LinkedHashSet<>();
        genres = new LinkedHashSet<>();
        directors = new LinkedHashSet<>();
    }
}
