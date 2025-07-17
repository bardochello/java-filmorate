package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
class FilmControllerTest {
    @Autowired
    private FilmController filmController;

    @Test
    public void testUpdateFilmSuccessfully() {
        Film initialFilm = new Film();
        initialFilm.setName("Initial Film");
        initialFilm.setDescription("Initial Description");
        initialFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        initialFilm.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        initialFilm.setMpa(mpa);

        Film createdFilm = filmController.create(initialFilm);

        Film updatedFilm = new Film();
        updatedFilm.setId(createdFilm.getId());
        updatedFilm.setName("Updated Film");
        updatedFilm.setDescription("Updated Description");
        updatedFilm.setReleaseDate(createdFilm.getReleaseDate());
        updatedFilm.setDuration(createdFilm.getDuration());
        updatedFilm.setMpa(createdFilm.getMpa());

        Film result = filmController.update(updatedFilm);
        assertThat(result)
                .hasFieldOrPropertyWithValue("name", "Updated Film")
                .hasFieldOrPropertyWithValue("description", "Updated Description");
    }
}