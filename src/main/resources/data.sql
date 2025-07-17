DELETE FROM likes;
DELETE FROM film_genres;
DELETE FROM films;
ALTER TABLE films ALTER COLUMN id RESTART WITH 1;
DELETE FROM mpa;
DELETE FROM genres;

INSERT INTO mpa (id, name) VALUES (1, 'G'), (2, 'PG'), (3, 'PG-13'), (4, 'R'), (5, 'NC-17');
INSERT INTO genres (id, name) VALUES (1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'),
                                     (5, 'Документальный'), (6, 'Боевик');