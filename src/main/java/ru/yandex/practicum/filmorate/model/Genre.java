package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public class Genre {
    public static final Genre COMEDY = new Genre(1, "Комедия");
    public static final Genre DRAMA = new Genre(2, "Драма");
    public static final Genre CARTOON = new Genre(3, "Мультфильм");
    public static final Genre THRILLER = new Genre(4, "Триллер");
    public static final Genre DOCUMENTARY = new Genre(5, "Документальный");
    public static final Genre ACTION_MOVIE = new Genre(6, "Боевик");
    private static final Map<Integer, Genre> idToGenres = new HashMap<>() {{
        put(COMEDY.id, COMEDY);
        put(DRAMA.id, DRAMA);
        put(CARTOON.id, CARTOON);
        put(THRILLER.id, THRILLER);
        put(DOCUMENTARY.id, DOCUMENTARY);
        put(ACTION_MOVIE.id, ACTION_MOVIE);
    }};
    private static final Map<String, Genre> genreNameToGenre = new HashMap<>() {{
        put("COMEDY", COMEDY);
        put("DRAMA", DRAMA);
        put("CARTOON", CARTOON);
        put("THRILLER", THRILLER);
        put("DOCUMENTARY", DOCUMENTARY);
        put("ACTION_MOVIE", ACTION_MOVIE);
    }};

    private final int id;
    private final String name;

    Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public static Genre valueOf(String genre) {
        return genreNameToGenre.get(genre);
    }

    @JsonCreator
    public static Genre forValues(@JsonProperty("id") int id) {
        return idToGenres.get(id);
    }
}
