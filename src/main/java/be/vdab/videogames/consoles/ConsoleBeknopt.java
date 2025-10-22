package be.vdab.videogames.consoles;

public record ConsoleBeknopt(
        long id,
        String name,
        String manufacturer,
        int releaseYear
) {
    public ConsoleBeknopt(Console console) {
        this(console.getId(), console.getName(), console.getManufacturer(), console.getReleaseYear());
    }
}
