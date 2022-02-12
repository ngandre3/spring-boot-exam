-- CREATE SCHEMA guessthatmovie
-- USE guessthatmovie
-- DROP TABLE IF EXISTS USER_ROLE;
-- DROP TABLE IF EXISTS SEC_ROLE;
-- DROP TABLE IF EXISTS SEC_USER;

-- Table Creation  =============================
CREATE TABLE SEC_USER
( 
  userId           	BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  userName         	VARCHAR(40) NOT NULL UNIQUE,
  encryptedPassword VARCHAR(128) NOT NULL,
  ENABLED           BIT NOT NULL 
) ;

CREATE TABLE SEC_ROLE
(
  roleId   	BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  roleName 	VARCHAR(30) NOT NULL UNIQUE
) ;

CREATE TABLE USER_ROLE
(
  userId 	BIGINT NOT NULL,
  roleId 	BIGINT NOT NULL,
  CONSTRAINT pk_userrole PRIMARY KEY (userId, roleId)
);

ALTER TABLE USER_ROLE
  ADD CONSTRAINT USER_ROLE_UK UNIQUE (userId, roleId);

ALTER TABLE USER_ROLE
  ADD CONSTRAINT USER_ROLE_FK1 FOREIGN KEY (userId) REFERENCES SEC_USER (userId);
 
ALTER TABLE USER_ROLE
  ADD CONSTRAINT USER_ROLE_FK2 FOREIGN KEY (roleId) REFERENCES SEC_ROLE (roleId);

INSERT INTO SEC_Role (roleName)
VALUES ('ROLE_USER'), ('ROLE_ADMIN');
 
-- ===================================
-- CREATE ADMIN ACCOUNT, make it have admin and user roles, 
-- username = 'admin'  //  password= 'secure'

INSERT INTO SEC_User (userName, encryptedPassword, ENABLED)
VALUES ('admin', '$2y$12$PGcsU2n02hrtg4zqAJOhle8tg/to0Q3Ouoiqg0zpMa6iQVGcGY5ue', 1);
 
INSERT INTO user_role (userId, roleId)
VALUES (1, 1), (1, 2);

-- ===================================
-- CREATE PLAYER USER ACCOUNTS, from previous top 5 players
-- password = "123"
INSERT INTO SEC_User (userName, encryptedPassword, ENABLED)
VALUES 	('Andrew', '$2y$12$X.mFaFK0BsWZN3zUGL7f4.vplqllP.IyFP3EPdN8HSlmFVx5hx6qO', 1),
		('Tom', '$2y$12$X.mFaFK0BsWZN3zUGL7f4.vplqllP.IyFP3EPdN8HSlmFVx5hx6qO', 1),
		('Aaron', '$2y$12$X.mFaFK0BsWZN3zUGL7f4.vplqllP.IyFP3EPdN8HSlmFVx5hx6qO', 1),
		('Russell', '$2y$12$X.mFaFK0BsWZN3zUGL7f4.vplqllP.IyFP3EPdN8HSlmFVx5hx6qO', 1),
		('Patrick', '$2y$12$X.mFaFK0BsWZN3zUGL7f4.vplqllP.IyFP3EPdN8HSlmFVx5hx6qO', 1),
        ('Kyler', '$2y$12$X.mFaFK0BsWZN3zUGL7f4.vplqllP.IyFP3EPdN8HSlmFVx5hx6qO', 1),
        ('Lamar', '$2y$12$X.mFaFK0BsWZN3zUGL7f4.vplqllP.IyFP3EPdN8HSlmFVx5hx6qO', 1);

INSERT INTO user_role (userId, roleId)
VALUES (2, 1), (3, 1), (4, 1), (5, 1), (6, 1), (7, 1), (8, 1);
 
COMMIT;