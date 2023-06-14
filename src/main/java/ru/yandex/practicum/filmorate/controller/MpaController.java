package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService service;

    public MpaController(MpaService mpaService) {
        this.service = mpaService;
    }

    @GetMapping
    public List<Mpa> getRatingMPAs(HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return service.getMpas();
    }

    @GetMapping("/{id}")
    public Mpa getRatingMPA(@PathVariable Integer id,
                            HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return service.getMpa(id);
    }
}
