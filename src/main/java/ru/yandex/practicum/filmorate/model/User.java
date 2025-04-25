package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Integer id; // Id пользователя
    private String email; // Электронная почта пользователя
    private String login; // Логин пользователя
    private String name; // Имя пользователя
    private LocalDate birthday; // Дата рождения пользователя
    private Set<Integer> friends = new HashSet<>(); //Друзья
}
