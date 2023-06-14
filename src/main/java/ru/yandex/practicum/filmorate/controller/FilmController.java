package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;
    private final FeedService feedService;

    public FilmController(FilmService service, FeedService feedService) {
        this.service = service;
        this.feedService = feedService;
    }

    @GetMapping
    public List<Film> getAllFilms(HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return service.getAllFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film,
                        HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return service.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film,
                           HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return service.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id,
                            HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return service.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable long id,
                        @PathVariable long userId,
                        HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        service.addLike(id, userId);
        feedService.createFeed(userId, id, "LIKE", "ADD");
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable long id,
                           @PathVariable long userId,
                           HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        service.removeLike(id, userId);
        feedService.createFeed(userId, id, "LIKE", "REMOVE");
    }

    @GetMapping("/popular")
    public List<Film> getPopulated(@RequestParam(required = false) Integer count,
                                   @RequestParam(required = false) Integer genreId,
                                   @RequestParam(required = false) Integer year,
                                   HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return service.getPopulated(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilms(@PathVariable long directorId,
                                       @RequestParam String sortBy,
                                       HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return service.getDirectorFilms(directorId, sortBy);
    }

    @GetMapping("/common")
    public List<Film> commonAndPopularFilm(@RequestParam Long userId,
                                           @RequestParam Long friendId,
                                           HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return service.commonAndPopularFilm(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> searchFilm(@RequestParam String query,
                                 @RequestParam("by") Optional<List<String>> searchBy,
                                 HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return service.searchFilm(query, searchBy.orElse(new ArrayList<>()));
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable long id,
                           HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        service.removeFilmById(id);
    }
}
