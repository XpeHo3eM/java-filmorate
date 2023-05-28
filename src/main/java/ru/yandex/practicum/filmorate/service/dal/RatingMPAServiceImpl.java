package ru.yandex.practicum.filmorate.service.dal;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.mpa.MpaEntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.RatingMpaService;
import ru.yandex.practicum.filmorate.storage.RatingMpaStorage;

import java.util.List;

@Service
public class RatingMPAServiceImpl implements RatingMpaService {
    private final RatingMpaStorage ratingMpaStorage;

    public RatingMPAServiceImpl(RatingMpaStorage ratingMpaStorage) {
        this.ratingMpaStorage = ratingMpaStorage;
    }

    @Override
    public List<Mpa> getRatingMPAs() {
        return ratingMpaStorage.getRatingMPAs();
    }

    @Override
    public Mpa getRatingMPA(Integer id) {
        Mpa mpaOnDb = ratingMpaStorage.getRatingMPA(id);

        if (mpaOnDb == null) {
            throw new MpaEntityNotFoundException(String.format("Рейтинг с ID = %s не найден", id));
        }

        return mpaOnDb;
    }
}
