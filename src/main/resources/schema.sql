--Включите в файл schema.sql создание таблиц.
--Если вам нужны некоторые данные в базе, их инициализация
--обычно описывается в файле data.sql — создайте его там же, где и schema.sql.
--Чтобы избежать ошибок, связанных с многократным применением скрипта к БД,
--добавьте условие IF NOT EXISTS при создании таблиц и индексов.
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS friendship CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS mpa CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS film_like CASCADE;
DROP TABLE IF EXISTS film_genres CASCADE;
DROP TABLE IF EXISTS film_mpa CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR NOT NULL,
    login VARCHAR NOT NULL,
    name VARCHAR,
    birthday DATE
);

CREATE TABLE IF NOT EXISTS friendship (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user1_id INT NOT NULL,
    user2_id INT NOT NULL,
    is_friends BOOLEAN,
    FOREIGN KEY (user1_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (user2_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS films (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL,
    description VARCHAR(200),
    releaseDate DATE,
    duration INT
);

CREATE TABLE IF NOT EXISTS genres (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS film_like (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id INT NOT NULL,
    film_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_mpa (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id INT NOT NULL,
    mpa_id INT NOT NULL,
    FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE,
    FOREIGN KEY (mpa_id) REFERENCES mpa (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_genres (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id INTEGER NOT NULL,
    genre_id INTEGER NOT NULL,
    FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE
);