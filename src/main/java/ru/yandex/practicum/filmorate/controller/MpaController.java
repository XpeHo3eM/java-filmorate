package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService service;

    public MpaController(MpaService mpaService) {
        this.service = mpaService;
    }

    @GetMapping
    public List<Mpa> getRatingMPAs() {
        return service.getMpas();
    }

    @GetMapping("/{id}")
    public Mpa getRatingMPA(@PathVariable Integer id) {
        return service.getMpa(id);
    }
}
