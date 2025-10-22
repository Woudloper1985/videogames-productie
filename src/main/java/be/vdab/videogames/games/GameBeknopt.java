package be.vdab.videogames.games;

import java.time.LocalDate;

public record GameBeknopt(
        long id,
        String title,
        String developer,
        LocalDate releaseDate,
        Genre genre
) {
    public GameBeknopt(Game game) {
        this(game.getId(), game.getTitle(), game.getDeveloper(), game.getReleaseDate(), game.getGenre());
    }
}