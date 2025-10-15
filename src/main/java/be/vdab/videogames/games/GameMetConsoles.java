package be.vdab.videogames.games;

import be.vdab.videogames.consoles.ConsoleBeknopt;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

record GameMetConsoles(
        String title,
        String developer,
        Genre genre,
        Set<ConsoleBeknopt> consoles) {
    GameMetConsoles(Game game) {
        this(
                game.getTitle(),
                game.getDeveloper(),
                game.getGenre(),
                game.getConsoles()
                        .stream()
                        .map(ConsoleBeknopt::new)
                        .sorted((c1, c2) -> c1.name().compareToIgnoreCase(c2.name())) // sorteren op naam
                        .collect(Collectors.toCollection(LinkedHashSet::new)) // behoud volgorde
        );
    }
}
