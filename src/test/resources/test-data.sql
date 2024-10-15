INSERT INTO mpa_ratings(name)
VALUES ('G');

INSERT INTO mpa_ratings(name)
VALUES ('PG');

INSERT INTO mpa_ratings(name)
VALUES ('PG-13');

INSERT INTO mpa_ratings(name)
VALUES ('R');

INSERT INTO mpa_ratings(name)
VALUES ('NC-17');

INSERT INTO genres(name)
VALUES ('Комедия');

INSERT INTO genres(name)
VALUES ('Драма');

INSERT INTO genres(name)
VALUES ('Мультфильм');

INSERT INTO genres(name)
VALUES ('Триллер');

INSERT INTO genres(name)
VALUES ('Документальный');

INSERT INTO genres(name)
VALUES ('Боевик');

INSERT INTO users (email, login, name, birthday)
values ('22@mail.ru', 'login', 'name', '2000-05-20');

INSERT INTO users (email, login, name, birthday)
values ('23@mail.ru', 'ggg', 'pasha', '2000-03-02');

INSERT INTO users (email, login, name, birthday)
values ('24@mail.ru', 'sss', 'dasha', '2000-04-05');

INSERT INTO films (name, description, release_date, duration, rating_id)
values ('aa', 'aa', '2020-05-20', 120, 2);

INSERT INTO films (name, description, release_date, duration, rating_id)
values ('bb', 'bb', '2020-05-20', 120, 2);

INSERT INTO films (name, description, release_date, duration, rating_id)
values ('cc', 'cc', '2020-05-20', 120, 1);

INSERT INTO likes (film_id, user_id)
values (1, 2);

INSERT INTO likes (film_id, user_id)
values(2, 1);

INSERT INTO films_genres (film_id, genre_id)
values(1, 2);

INSERT INTO films_genres (film_id, genre_id)
values(1, 3);

INSERT INTO films_genres (film_id, genre_id)
values(2, 1);

INSERT INTO films_genres (film_id, genre_id)
values(3, 4);

INSERT INTO friends (user_id, friend_id, approved)
values(1, 2, false)



