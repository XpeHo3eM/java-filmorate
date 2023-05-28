package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.RatingMpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmorateApplicationTests {
    private final GenreStorage genreStorage;
    private final RatingMpaStorage ratingMpaStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    private static Film film1;
    private static Film film2;
    private static User user1;
    private static User user2;

    @BeforeEach
    void generateObjects() {
        film1 = new Film(0,
                "film1",
                "description1",
                LocalDate.now(),
                99,
                Mpa.NC17);
        film2 = new Film(1,
                "film2",
                "description2",
                LocalDate.of(1990, 03, 13),
                213,
                Mpa.G);
        user1 = User.builder()
                .login("login1")
                .email("email1")
                .name("name1")
                .birthday(LocalDate.now().minusDays(66))
                .build();
        user2 = User.builder()
                .login("login2")
                .email("email2")
                .name("name2")
                .birthday(LocalDate.of(2001, 01, 30))
                .build();
    }

    @Test
    void shouldReturn6Genre() {
        assertEquals(6, genreStorage.getGenres().size(), "Некорректно количество жанров");
    }

    @Test
    void shouldGetCorrectGenre() {
        assertEquals(Genre.COMEDY, genreStorage.getGenre(Genre.COMEDY.getId()),
                "Некорректно извлекается жанр фильма");
        assertEquals(Genre.CARTOON, genreStorage.getGenre(Genre.CARTOON.getId()),
                "Некорректно извлекается жанр фильма");
        assertEquals(Genre.ACTION_MOVIE, genreStorage.getGenre(Genre.ACTION_MOVIE.getId()),
                "Некорректно извлекается жанр фильма");
    }

    @Test
    void shouldReturn5RatingMPAs() {
        assertEquals(5, ratingMpaStorage.getRatingMPAs().size(),
                "Некорректное количество вариаций рейтинга");
    }

    @Test
    void shouldGetCorrectRagingMPA() {
        assertEquals(Mpa.G, ratingMpaStorage.getRatingMPA(Mpa.G.getId()),
                "Некорректно извлекается рейтинг фильма");
        assertEquals(Mpa.PG13, ratingMpaStorage.getRatingMPA(Mpa.PG13.getId()),
                "Некорректно извлекается рейтинг фильма");
        assertEquals(Mpa.NC17, ratingMpaStorage.getRatingMPA(Mpa.NC17.getId()),
                "Некорректно извлекается рейтинг фильма");
    }

    @Test
    void shouldReturnFilm() {
        assertNotNull(filmStorage.addFilm(film1),
                "Не вернулся фильм при добавлении");
        assertEquals(film1, filmStorage.getAllFilms().get(0),
                "Не вернулся фильм после запроса");
    }

    @Test
    void shouldAddFilms() {
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);

        List<Film> films = filmStorage.getAllFilms();

        assertTrue(films.size() > 1,
                "Количество фильмов меньше 2");
        assertEquals(film1, films.get(0),
                "Не совпадает фильм после добавления");
        assertEquals(film2, films.get(1),
                "Не совпадает второй фильм после добавления");
    }

    @Test
    void shouldUpdateFilm() {
        film1 = filmStorage.addFilm(film1);
        film1.setName("UpdatedFilm1");
        filmStorage.updateFilm(film1);

        assertEquals(1, filmStorage.getAllFilms().size(),
                "Количество фильмов не равно 1");

        assertEquals(film1, filmStorage.getAllFilms().get(0),
                "Не обнаружен обновленный фильм");
    }

    @Test
    void shouldReturnUser() {
        assertNotNull(userStorage.addUser(user1),
                "Не вернулся пользователь при добавлении");
        assertEquals(user1, userStorage.getAllUsers().get(0),
                "Не вернулся пользователь после запроса");
    }

    @Test
    void shouldAddUsers() {
        userStorage.addUser(user1);
        userStorage.addUser(user2);

        List<User> users = userStorage.getAllUsers();

        assertNotNull(users,
                "Не вернулись пользователи");
        assertTrue(users.size() > 1,
                "Количество пользователей меньше 2");
        assertEquals(user1, userStorage.getAllUsers().get(0),
                "Не совпадает пользователь после добавления");
        assertEquals(user2, userStorage.getAllUsers().get(1),
                "Не совпадает второй пользователь после добавления");
    }

    @Test
    void shouldUpdateUser() {
        user1 = userStorage.addUser(user1);
        user1.setName("UpdatedUser1");
        userStorage.updateUser(user1);

        assertEquals(1, userStorage.getAllUsers().size(),
                "Количество пользователей не равно 1");
        assertEquals(user1, userStorage.getAllUsers().get(0),
                "Не обнаружен обновленный пользователь");
    }

    @Test
    void shouldAddFriend() {
        userStorage.addUser(user1);
        userStorage.addUser(user2);

        assertNotNull(userStorage.addFriend(1L, 2L),
                "Не добавился друг");

        List<User> users = userStorage.getAllUsers();

        assertNotNull(users,
                "Не вернулись пользователи");
        assertTrue(users.size() > 1,
                "Вернулось пользователей меньше 2");
        assertNotNull(userStorage.getFriends(1L),
                "Вернулся список друзей первого пользователя null");
        assertFalse(userStorage.getFriends(1L).isEmpty(),
                "Список друзей первого пользователя пуст");
        assertNotNull(userStorage.getFriends(2L),
                "Вернулся список друзей второго пользователя null");
        assertTrue(userStorage.getFriends(2L).isEmpty(),
                "Список друзей второго пользователя пуст");
    }

    @Test
    void shouldRemoveFriend() {
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addFriend(1L, 2L);

        assertTrue(userStorage.removeFriend(1L, 2L).isEmpty(),
                "Не удалился друг");
    }
}
