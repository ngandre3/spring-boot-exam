-- CREATE SCHEMA guessthatmovie
-- USE guessthatmovie

-- DROP TABLE IF EXISTS movie;
-- DROP TABLE IF EXISTS scoreboard;

-- Table Creation  =============================
CREATE TABLE scoreboard
(
  scoreId		BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  userId    	BIGINT UNIQUE NOT NULL,
  games_played	INT DEFAULT 0,
  score			INT DEFAULT 0
);
 
ALTER TABLE scoreboard
  ADD CONSTRAINT scoreboard_fk1 FOREIGN KEY (userId) REFERENCES sec_user (userId);
 
CREATE TABLE movie
(
  movieId		BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  movieName		VARCHAR(100) NOT NULL UNIQUE
);

-- ADD PLAYERS SCORES ================================        
INSERT INTO scoreboard (userId, games_played, score)
VALUES	(1, 1000, -1000),
		(2, 60, 100),
		(3, 20, 60),
        (4, 14, 10),
        (5, 8, 5),
        (6, 9, -20),
        (7, 11, -15),
        (8, 8, -30);

-- ADD G-RATED Animation MOVIES ================================
INSERT INTO movie (movieName)
VALUES  ('Finding Nemo'),
		('Monsters University'),
        ('Toy Story'),
        ('Wall-E'),
        ('The Lion King'),
        ('Aladdin'),
        ('Cars'),
        ('Mulan'),
        ('Beauty and the Beast'),
        ('The Jungle Book'),
        ('Winnie the Pooh');
        
COMMIT;        