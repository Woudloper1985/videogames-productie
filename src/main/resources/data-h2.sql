-- Testdata for H2 (copied from videogames.sql)

-- consoles
insert into consoles (name, manufacturer, releaseYear) values
('PlayStation 5','Sony',2020),
('Xbox Series X','Microsoft',2020),
('Switch','Nintendo',2017);

-- games
insert into games (title, developer, releaseDate, genre) values
('Game A','DevStudio1','2020-11-12','ACTION'),
('Game B','DevStudio2','2020-11-12','ADVENTURE'),
('Game C','DevStudio3','2021-03-15','RPG');

-- consolesgames
insert into consolesgames (gameId, consoleId) values
(1,1),(1,2),
(2,1),(2,3),
(3,2),(3,3);
