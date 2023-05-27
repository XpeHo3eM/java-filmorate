DELETE FROM genres;
DELETE FROM ratingsMPA;

INSERT INTO genres(id, genre)
VALUES (1, 'COMEDY'),
       (2, 'DRAMA'),
       (3, 'CARTOON'),
       (4, 'THRILLER'),
       (5, 'DOCUMENTARY'),
       (6, 'ACTION_MOVIE');

INSERT INTO ratingsMPA(id, rating)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG13'),
       (4, 'R'),
       (5, 'NC17');
