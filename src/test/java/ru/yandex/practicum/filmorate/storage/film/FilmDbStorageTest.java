package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class})
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        // Очистка таблиц перед каждым тестом для изоляции
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM mpa");
        // Вставка тестовых данных для MPA
        jdbcTemplate.update("INSERT INTO mpa (id, name) VALUES (1, 'G')");
    }

    @Test
    public void testCreateFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Film createdFilm = filmStorage.create(film);

        assertThat(createdFilm)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", createdFilm.getId()) // Проверяем, что ID присвоен
                .hasFieldOrPropertyWithValue("name", "Test Film")
                .hasFieldOrPropertyWithValue("description", "Description");
    }

    @Test
    public void testFindFilmById() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        Film createdFilm = filmStorage.create(film);

        Film foundFilm = filmStorage.findById(createdFilm.getId());

        assertThat(foundFilm)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", createdFilm.getId())
                .hasFieldOrPropertyWithValue("name", "Test Film")
                .hasFieldOrPropertyWithValue("description", "Description");
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        Film createdFilm = filmStorage.create(film);

        createdFilm.setName("Updated Film");
        createdFilm.setDescription("Updated Description");
        filmStorage.update(createdFilm);

        Film updatedFilm = filmStorage.findById(createdFilm.getId());

        assertThat(updatedFilm)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", createdFilm.getId())
                .hasFieldOrPropertyWithValue("name", "Updated Film")
                .hasFieldOrPropertyWithValue("description", "Updated Description");
    }

    @Test
    public void testFindAllFilms() {
        Film film1 = new Film();
        film1.setName("Test Film 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        film1.setMpa(mpa1);
        filmStorage.create(film1);

        Film film2 = new Film();
        film2.setName("Test Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2000, 1, 2));
        film2.setDuration(150);
        Mpa mpa2 = new Mpa();
        mpa2.setId(1);
        film2.setMpa(mpa2);
        filmStorage.create(film2);

        Collection<Film> films = filmStorage.findAll();

        assertThat(films)
                .isNotNull()
                .hasSize(2)
                .extracting("name")
                .containsExactlyInAnyOrder("Test Film 1", "Test Film 2");
    }

    @Test
    public void testDeleteFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        Film createdFilm = filmStorage.create(film);

        filmStorage.delete(createdFilm.getId());

        Film foundFilm = filmStorage.findById(createdFilm.getId());

        assertThat(foundFilm).isNull();
    }
}