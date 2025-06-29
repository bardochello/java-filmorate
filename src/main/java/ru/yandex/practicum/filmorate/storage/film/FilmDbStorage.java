package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null);
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        saveGenres(film);
        saveLikes(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        // Получаем существующий фильм
        Film existingFilm = findById(film.getId());
        if (existingFilm == null) {
            return null; // Это обрабатывается в FilmService
        }

        // Формируем SQL с обновлением только указанных полей
        StringBuilder sql = new StringBuilder("UPDATE films SET ");
        var params = new java.util.ArrayList<>();
        int paramIndex = 1;

        if (film.getName() != null) {
            sql.append("name = ?");
            params.add(film.getName());
        } else {
            sql.append("name = ?");
            params.add(existingFilm.getName());
        }

        if (film.getDescription() != null) {
            sql.append(", description = ?");
            params.add(film.getDescription());
        } else {
            sql.append(", description = ?");
            params.add(existingFilm.getDescription());
        }

        if (film.getReleaseDate() != null) {
            sql.append(", release_date = ?");
            params.add(Date.valueOf(film.getReleaseDate()));
        } else {
            sql.append(", release_date = ?");
            params.add(existingFilm.getReleaseDate() != null ? Date.valueOf(existingFilm.getReleaseDate()) : null);
        }

        if (film.getDuration() != 0) {
            sql.append(", duration = ?");
            params.add(film.getDuration());
        } else {
            sql.append(", duration = ?");
            params.add(existingFilm.getDuration());
        }

        if (film.getMpa() != null) {
            sql.append(", mpa_id = ?");
            params.add(film.getMpa().getId());
        } else {
            sql.append(", mpa_id = ?");
            params.add(existingFilm.getMpa().getId());
        }

        sql.append(" WHERE id = ?");
        params.add(film.getId());

        jdbcTemplate.update(sql.toString(), params.toArray());

        deleteGenres(film.getId());
        saveGenres(film);
        deleteLikes(film.getId());
        saveLikes(film);
        return findById(film.getId());
    }

    @Override
    public Film findById(int id) {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f JOIN mpa m ON f.mpa_id = m.id WHERE f.id = ?";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper(), id);
        return films.isEmpty() ? null : films.get(0);
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f JOIN mpa m ON f.mpa_id = m.id";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper());
        return films;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private RowMapper<Film> filmRowMapper() {
        return (rs, rowNum) -> {
            try {
                Film film = new Film();
                film.setId(rs.getInt("id"));
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date") != null ? rs.getDate("release_date").toLocalDate() : null);
                film.setDuration(rs.getInt("duration"));
                Mpa mpa = new Mpa();
                mpa.setId(rs.getInt("mpa_id"));
                mpa.setName(rs.getString("mpa_name"));
                film.setMpa(mpa);
                loadGenres(film);
                loadLikes(film);
                return film;
            } catch (SQLException e) {
                throw new RuntimeException("Ошибка при маппинге фильма: " + e.getMessage(), e);
            }
        };
    }

    private void saveGenres(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Integer> genreIds = new HashSet<>(); // Множество для уникальных ID жанров
            for (Genre genre : film.getGenres()) {
                if (genreIds.add(genre.getId())) { // Добавляем только уникальные жанры
                    String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
                    jdbcTemplate.update(sql, film.getId(), genre.getId());
                }
            }
        }
    }

    private void deleteGenres(int filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private void loadGenres(Film film) {
        String sql = "SELECT g.* FROM genres g JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, film.getId());
        film.setGenres(genres);
    }

    private void saveLikes(Film film) {
        if (film.getLikes() != null && !film.getLikes().isEmpty()) {
            for (Integer userId : film.getLikes()) {
                String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
                jdbcTemplate.update(sql, film.getId(), userId);
            }
        }
    }

    private void deleteLikes(int filmId) {
        String sql = "DELETE FROM likes WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private void loadLikes(Film film) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Integer> likes = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user_id"), film.getId());
        film.setLikes(new HashSet<>(likes));
    }
}