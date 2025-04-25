package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
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
    private Set<Integer> likes = new HashSet<>(); //Лайки
}
