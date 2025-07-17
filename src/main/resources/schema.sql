CREATE TABLE IF NOT EXISTS mpa (
                                   id INT AUTO_INCREMENT PRIMARY KEY,
                                   name VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS genres (
                                      id INT AUTO_INCREMENT PRIMARY KEY,
                                      name VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS users (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     email VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    birthday DATE
    );

CREATE TABLE IF NOT EXISTS films (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    release_date DATE,
    duration INT,
    mpa_id INT,
    FOREIGN KEY (mpa_id) REFERENCES mpa(id)
    );

CREATE TABLE IF NOT EXISTS film_genres (
    film_id INT,
    genre_id INT,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(id),
    FOREIGN KEY (genre_id) REFERENCES genres(id)
    );

CREATE TABLE IF NOT EXISTS likes (
                                     film_id INT,
                                     user_id INT,
                                     PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
    );

CREATE TABLE IF NOT EXISTS friends (
                                       user_id INT,
                                       friend_id INT,
                                       status VARCHAR(50),
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (friend_id) REFERENCES users(id)
    );
