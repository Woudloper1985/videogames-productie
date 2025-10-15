package be.vdab.videogames.consoles;

public record ConsoleBeknopt(
        String name,
        String manufacturer,
        int releaseYear
) {
    public ConsoleBeknopt(Console console) {
        this(console.getName(), console.getManufacturer(), console.getReleaseYear());
    }
}
