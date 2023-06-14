package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.RecommendationsService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;
    private final RecommendationsService recommendationsService;
    private final FeedService feedService;

    public UserController(UserService service, RecommendationsService recommendationsService, FeedService feedService) {
        this.service = service;
        this.recommendationsService = recommendationsService;
        this.feedService = feedService;
    }

    @GetMapping
    public List<User> getAllUsers(HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return service.getAllUsers();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user,
                        HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return service.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user,
                           HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return service.updateUser(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id,
                            HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return service.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addToFriend(@PathVariable long id,
                            @PathVariable long friendId,
                            HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        service.addFriend(id, friendId);
        feedService.createFeed(id, friendId, "FRIEND", "ADD");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFromFriends(@PathVariable long id,
                                  @PathVariable long friendId,
                                  HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        service.removeFriend(id, friendId);
        feedService.createFeed(id, friendId, "FRIEND", "REMOVE");
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable long id,
                                 HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return service.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable long id,
                                       @PathVariable long otherId,
                                       HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return service.getMutualFriends(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommended(@PathVariable long id,
                                     HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return recommendationsService.getRecommended(id);
    }

    @DeleteMapping("/{userId}")
    public void deleteFromFriends(@PathVariable long userId,
                                  HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        service.removeUserById(userId);
    }

    @GetMapping("/{id}/feed")
    public List<FeedEvent> getAllFeed(@PathVariable long id,
                                      HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return feedService.getFeedByUserId(id);
    }
}
