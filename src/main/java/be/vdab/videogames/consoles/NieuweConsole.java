package be.vdab.videogames.consoles;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

record NieuweConsole(
        @NotBlank String name,
        @NotBlank String manufacturer,
        @Min(1970) int releaseYear
) {
}