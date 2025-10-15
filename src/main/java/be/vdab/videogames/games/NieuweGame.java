package be.vdab.videogames.games;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;

public record NieuweGame(
        @NotBlank String title,
        @NotBlank String developer,
        @NotNull @PastOrPresent LocalDate releaseDate,
        @NotEmpty Set<@Positive Long> consoleIds, // minstens één console verplicht
        @NotNull Genre genre //genre verplicht in te voeren
) {
}
