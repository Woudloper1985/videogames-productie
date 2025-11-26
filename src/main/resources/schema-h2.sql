-- Schema for H2 (in-memory) based on videogames.sql

-- Drop existing tables if they exist (safety when reloading)
drop table if exists consolesgames;
drop table if exists games;
drop table if exists consoles;

-- Tabel: consoles
create table consoles (
    id bigint auto_increment primary key,
    name varchar(100) not null,
    manufacturer varchar(100) not null,
    releaseYear int not null,
    constraint uq_console_name unique (name)
);

-- Tabel: games
create table games (
    id bigint auto_increment primary key,
    title varchar(150) not null,
    developer varchar(100) not null,
    releaseDate date not null,
    genre varchar(100) not null,
    constraint uq_game_title unique (title)
);

-- Tabel: consolesgames (ManyToMany)
create table consolesgames (
    gameId bigint not null,
    consoleId bigint not null,
    primary key (gameId, consoleId),
    constraint fk_games_consoles foreign key (gameId) references games(id),
    constraint fk_consoles_games foreign key (consoleId) references consoles(id)
);
