package be.vdab.videogames.consoles;

import be.vdab.videogames.games.Game;
import jakarta.persistence.*;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "consoles")
public class Console {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String manufacturer;
    private int releaseYear;

    @ManyToMany(mappedBy = "consoles")
    @OrderBy("title")
    private Set<Game> games = new LinkedHashSet<>();

    public Console(String name, String manufacturer, int releaseYear) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.releaseYear = releaseYear;
        games = new LinkedHashSet<>();
    }

    protected Console() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setManufacturer(String manufacturer) {
//        this.manufacturer = manufacturer;
//    }
//
//    public void setReleaseYear(int releaseYear) {
//        this.releaseYear = releaseYear;
//    }

    public Set<Game> getGames() {
        return games; // modifiable, om console te kunnen verwijderen.
    }

    public void addGame(Game game) {
        games.add(game);
    }

    public void removeGame(Game game) {
        games.remove(game);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Console console)) return false;
        return Objects.equals(getName(), console.getName()); //unieke constraint in db.
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }
}