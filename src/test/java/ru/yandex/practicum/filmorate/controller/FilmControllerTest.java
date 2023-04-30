package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTest {
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_RELEASE_DATE_TIME = LocalDate.of(1895, Month.DECEMBER, 28);
    private Film film;

    @Autowired
    private FilmController controller;

    @BeforeEach
    public void createCorrectFilm() {
        film = Film.builder()
                .name("Scary movie")
                .description("Comedy")
                .releaseDate(LocalDate.of(2003, Month.APRIL, 17))
                .duration(93)
                .build();
    }

    @Test
    public void shouldCreateFilmWithCorrectParam() {
        controller.addFilm(film);

        List<Film> films = controller.getAllFilms();

        assertTrue(films.contains(film), "Не найден корректный фильм");
    }

    @Test
    public void shouldNotCreateFilmWithIncorrectName() {
        film.setName(" ");
        assertThrows(ValidationException.class,
                () -> controller.addFilm(film),
                "Пропустило с именем фильма из пробела");

        film.setName("");
        assertThrows(ValidationException.class,
                () -> controller.addFilm(film),
                "Пропустило с пустым именем фильма");
    }

    @Test
    public void shouldNotCreateFilmWithIncorrectDescription() {
        String description = "1".repeat(MAX_DESCRIPTION_LENGTH + 1);
        film.setDescription(description);

        assertThrows(ValidationException.class,
                () -> controller.addFilm(film),
                "Пропустило с описанием более " + MAX_DESCRIPTION_LENGTH + " символов");
    }

    @Test
    public void shouldNotCreateFilmWithIncorrectReleaseDate() {
        film.setReleaseDate(MIN_RELEASE_DATE_TIME.minusDays(1));

        assertThrows(ValidationException.class,
                () -> controller.addFilm(film),
                "Пропустило дату релиза менее минимальной " + MIN_RELEASE_DATE_TIME);
    }

    @Test
    public void shouldNotCreateFilmWithIncorrectDuration() {
        film.setDuration(-1);
        assertThrows(ValidationException.class,
                () -> controller.addFilm(film),
                "Пропустило отрицательную длительность");

        film.setDuration(0);
        assertThrows(ValidationException.class,
                () -> controller.addFilm(film),
                "Пропустило нулевую длительность");
    }

    @Test
    public void shouldUpdateFilm() {
        Film updatedFilm = Film.builder()
                .id(controller.addFilm(film).getId())
                .name("Scary movie")
                .description("Updated description")
                .releaseDate(LocalDate.of(2003, Month.APRIL, 17))
                .duration(93)
                .build();

        assertEquals(updatedFilm, controller.updateFilm(updatedFilm), "Не обновлен фильм");
    }

    @Test
    public void shouldThrowExceptionWithIncorrectFilmParam() {
        controller.addFilm(film);

        assertThrows(ValidationException.class,
                () -> controller.updateFilm(Film.builder()
                        .name("Scary movie")
                        .description("Comedy")
                        .releaseDate(LocalDate.of(2003, Month.APRIL, 17))
                        .duration(93)
                        .build()),
                "Пропустило обновление фильма без ID");
    }
}
