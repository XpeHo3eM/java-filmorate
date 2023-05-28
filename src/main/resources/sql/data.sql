DELETE FROM genres;
DELETE FROM ratingsMPA;

INSERT INTO genres(genre)
VALUES ('COMEDY'),
       ('DRAMA'),
       ('CARTOON'),
       ('THRILLER'),
       ('DOCUMENTARY'),
       ('ACTION_MOVIE');

INSERT INTO ratingsMPA(rating)
VALUES ('G'),
       ('PG'),
       ('PG13'),
       ('R'),
       ('NC17');
