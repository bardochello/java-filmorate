package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    private Integer id; // Id фильма
    private String name; // Название фильма
    private String description; // Описание фильма
    private LocalDate releaseDate; // Дата релиза
    private int duration; // Продолжительность фильма в минутах
}
