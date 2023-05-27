package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.RatingMpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class RatingMpaController {
    private final RatingMpaService service;

    public RatingMpaController(RatingMpaService ratingMpaService) {
        this.service = ratingMpaService;
    }

    @GetMapping
    public List<Mpa> getRatingMPAs() {
        return service.getRatingMPAs();
    }

    @GetMapping("/{id}")
    public Mpa getRatingMPA(@PathVariable Integer id) {
        return service.getRatingMPA(id);
    }
}
