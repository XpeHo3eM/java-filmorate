package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public class Mpa {
    public static final Mpa G = new Mpa(1, "G");
    public static final Mpa PG = new Mpa(2, "PG");
    public static final Mpa PG13 = new Mpa(3, "PG-13");
    public static final Mpa R = new Mpa(4, "R");
    public static final Mpa NC17 = new Mpa(5, "NC-17");
    private static final Map<Integer, Mpa> idToMpas = new HashMap<>() {{
        put(G.id, G);
        put(PG.id, PG);
        put(PG13.id, PG13);
        put(R.id, R);
        put(NC17.id, NC17);
    }};
    private static final Map<String, Mpa> mpaNameToMpa = new HashMap<>() {{
        put("G", G);
        put("PG", PG);
        put("PG13", PG13);
        put("R", R);
        put("NC17", NC17);
    }};

    private final int id;
    private final String name;

    Mpa(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public static Mpa valueOf(String mpa) {
        return mpaNameToMpa.get(mpa);
    }

    @JsonCreator
    public static Mpa forValues(@JsonProperty("id") int id) {
        return idToMpas.get(id);
    }
}
