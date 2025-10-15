package be.vdab.videogames.consoles;

import be.vdab.videogames.games.GameBeknopt;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

record ConsoleMetGames(
        String name,
        String manufacturer,
        int releaseYear,
        Set<GameBeknopt> games) {

    ConsoleMetGames(Console console) {
        this(
                console.getName(),
                console.getManufacturer(),
                console.getReleaseYear(),
                console.getGames()
                        .stream()
                        .map(GameBeknopt::new)
                        .sorted((g1, g2) -> g1.title().compareToIgnoreCase(g2.title())) // sorteren op titel
                        .collect(Collectors.toCollection(LinkedHashSet::new)) // behoud volgorde
        );
    }
}

