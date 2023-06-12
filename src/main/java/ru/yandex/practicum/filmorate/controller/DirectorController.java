package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
public class DirectorController {
    private DirectorService service;

    public DirectorController(DirectorService service) {
        this.service = service;
    }

    @GetMapping
    public List<Director> getAllDirectors() {
        return service.getAllDirectors();
    }

    @PostMapping
    public Director addDirector(@Valid @RequestBody Director director) {
        return service.addDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        return service.updateDirector(director);
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable long id) {
        return service.getDirectorById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void removeDirector(@PathVariable long id) {
        service.removeDirector(id);
    }
}