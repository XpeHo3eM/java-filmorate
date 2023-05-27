package ru.yandex.practicum.filmorate.service.dal;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.RatingMpaService;
import ru.yandex.practicum.filmorate.storage.RatingMpaStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class RatingMPAServiceImpl implements RatingMpaService {
    private final RatingMpaStorage ratingMpaStorage;

    public RatingMPAServiceImpl(RatingMpaStorage ratingMpaStorage) {
        this.ratingMpaStorage = ratingMpaStorage;
    }

    @Override
    public List<Mpa> getRatingMPAs() {
        return ratingMpaStorage.getRatingMPAs().orElse(new ArrayList<>());
    }

    @Override
    public Mpa getRatingMPA(Integer id) {
        return ratingMpaStorage.getRatingMPA(id).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Рейтинг с ID = %s не найден", id))
        );
    }
}
