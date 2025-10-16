package be.vdab.videogames.games;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

enum Genre {
    ACTION,
    ADVENTURE,
    METROIDVANIA,
    OTHER,
    PLATFORMER,
    RACING,
    ROGUELIKE, //naderhand toegevoegd
    RPG,
    SHOOTER,
    SOULSLIKE,
    SPORTS,
    STRATEGY;

    public static List<Genre> sortedValues() {
        return Arrays.stream(values())
                .sorted(Comparator.comparing(Enum::name))
                .toList();
    }
}