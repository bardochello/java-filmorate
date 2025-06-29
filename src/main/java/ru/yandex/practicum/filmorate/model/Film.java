package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film {
    private Integer id; // Id фильма

    private String name; // Название фильма

    private String description; // Описание фильма

    private LocalDate releaseDate; // Дата релиза

    private int duration;// Продолжительность фильма в минутах

    private Mpa mpa; // Рейтинг MPA

    private List<Genre> genres = new ArrayList<>(); // Список жанров фильма

    private Set<Integer> likes = new HashSet<>(); //Лайки
}
