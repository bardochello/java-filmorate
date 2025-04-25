package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTest {

    @Autowired
    private FilmController filmController;

    @Test
    public void testCreateFilmWithEmptyName() {
        Film film = new Film();
        film.setName(""); // Пустое имя
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.create(film),
                "Ожидается исключение для пустого имени фильма");
    }

    @Test
    public void testUpdateFilmSuccessfully() {
        Film initialFilm = new Film();
        initialFilm.setName("Initial Film");
        initialFilm.setDescription("Initial Description");
        initialFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        initialFilm.setDuration(120);
        Film createdFilm = filmController.create(initialFilm);

        Film updatedFilm = new Film();
        updatedFilm.setId(createdFilm.getId());
        updatedFilm.setName("Updated Film");
        updatedFilm.setDescription("Updated Description");
        updatedFilm.setReleaseDate(createdFilm.getReleaseDate()); // Сохраняем исходную дату
        updatedFilm.setDuration(createdFilm.getDuration());      // Сохраняем исходную продолжительность

        Film result = filmController.update(updatedFilm);
        assertEquals("Updated Film", result.getName(), "Имя фильма должно обновиться");
        assertEquals("Updated Description", result.getDescription(), "Описание фильма должно обновиться");
    }

    @Test
    public void testCreateFilmWithLongDescription() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("a".repeat(201)); // Описание длиннее 200 символов
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.create(film),
                "Ожидается исключение для слишком длинного описания");
    }

    @Test
    public void testCreateFilmWithInvalidReleaseDate() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27)); // Раньше 28.12.1895
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.create(film),
                "Ожидается исключение для некорректной даты релиза");
    }

    @Test
    public void testCreateFilmWithNegativeDuration() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-10); // Отрицательная продолжительность

        assertThrows(ValidationException.class, () -> filmController.create(film),
                "Ожидается исключение для отрицательной продолжительности");
    }

    @Test
    public void testCreateFilmSuccessfully() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film createdFilm = filmController.create(film);
        assertNotNull(createdFilm.getId(), "ID созданного фильма не должен быть null");
        assertEquals("Name", createdFilm.getName(), "Имя фильма должно совпадать");
    }
}