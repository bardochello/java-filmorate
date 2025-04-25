package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAll(); // Возвращает список всех фильмов в памяти

    Film create(Film film); // Создание фильма

    Film update(Film film); // Обновление фильма

    Film findById(int id); // Возвращает фильм по id

    void delete(int id); // Удаляет фильм
}
