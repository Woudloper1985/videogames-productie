-- ===========================================
-- Charset instellen
-- ===========================================
set names utf8mb4;
set charset utf8mb4;

-- ===========================================
-- Database aanmaken
-- ===========================================
drop database if exists videogames;
create database videogames charset utf8mb4;
use videogames;

-- ===========================================
-- Tabel: consoles
-- ===========================================
create table consoles (
    id int unsigned not null auto_increment primary key,
    name varchar(100) not null,
    manufacturer varchar(100) not null,
    releaseYear year not null,
    constraint uq_console_name unique (name)
);

-- ===========================================
-- Tabel: games
-- ===========================================
create table games (
    id int unsigned not null auto_increment primary key,
    title varchar(150) not null,
    developer varchar(100) not null,
    releaseDate date not null,
    genre varchar(100) not null,
    constraint uq_game_title unique (title)
);

-- ===========================================
-- Tabel: consolesgames (ManyToMany)
-- ===========================================
create table consolesgames (
    gameId int unsigned not null,
    consoleId int unsigned not null,
    primary key (gameId, consoleId),
    constraint fk_games_consoles foreign key (gameId) references games(id),
    constraint fk_consoles_games foreign key (consoleId) references consoles(id)
);

-- ===========================================
-- Testdata: consoles
-- ===========================================
insert into consoles (name, manufacturer, releaseYear) values
('PlayStation 5','Sony',2020),
('Xbox Series X','Microsoft',2020),
('Switch','Nintendo',2017);

-- ===========================================
-- Testdata: games
-- ===========================================
insert into games (title, developer, releaseDate, genre) values
('Game A','DevStudio1','2020-11-12','ACTION'),
('Game B','DevStudio2','2020-11-12','ADVENTURE'),
('Game C','DevStudio3','2021-03-15','RPG');

-- ===========================================
-- Testdata: consolesgames
-- ===========================================
insert into consolesgames (gameId, consoleId) values
(1,1),(1,2),
(2,1),(2,3),
(3,2),(3,3);

-- ===========================================
-- App-gebruiker
-- ===========================================
create user 'user'@'localhost' identified by 'user';
grant select, insert, update, delete, alter on videogames.* to 'user';