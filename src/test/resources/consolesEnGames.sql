insert into consoles(name, manufacturer, releaseYear)
values ('TestConsole 1', 'Sony', 2020),
       ('TestConsole 2', 'Microsoft', 2020),
       ('TestConsole 3', 'Nintendo', 2017);
insert into games(title, developer, releaseDate, genre)
values ('TestGame 1', 'Naughty Dog', '2020-06-19', 'ACTION'),
       ('TestGame 2', '343 Industries', '2021-12-08', 'SHOOTER'),
       ('TestGame 3', 'Nintendo', '2020-03-20', 'SHOOTER');
insert into consolesgames(gameId, consoleId)
select g.id, c.id
from games g, consoles c
where (g.title = 'TestGame 1' and c.name = 'TestConsole 1')
   or (g.title = 'TestGame 2' and c.name = 'TestConsole 1')
   or (g.title = 'TestGame 2' and c.name = 'TestConsole 3')
   or (g.title = 'TestGame 3' and c.name = 'TestConsole 3')
   or (g.title = 'TestGame 1' and c.name = 'TestConsole 3'); -- TestGame 1 heb ik ook op TestConsole 3