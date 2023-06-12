package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;
    private final FeedService feedService;

    @Autowired
    public FilmController(FilmService service, FeedService feedService) {
        this.service = service;
        this.feedService = feedService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return service.getAllFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return service.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return service.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        return service.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        service.addLike(id, userId);
        feedService.createFeed(userId, id, "LIKE", "ADD");
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        service.removeLike(id, userId);
        feedService.createFeed(userId, id, "LIKE", "REMOVE");
    }

    @GetMapping("/popular")
    public List<Film> getPopulated(@RequestParam(required = false) Integer count,
                                   @RequestParam(required = false) Integer genreId,
                                   @RequestParam(required = false) Integer year) {
        return service.getPopulated(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilms(@PathVariable long directorId, @RequestParam String sortBy) {
        return service.getDirectorFilms(directorId, sortBy);
    }

    @GetMapping("/common")
    public List<Film> commonAndPopularFilm(@RequestParam Long userId, @RequestParam Long friendId) {
        return service.commonAndPopularFilm(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> searchFilm(@RequestParam String query, @RequestParam("by") Optional<List<String>> searchBy) {
        return service.searchFilm(query, searchBy.orElse(new ArrayList<>()));
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable long id) {
        service.removeFilmById(id);
    }
}
