package be.vdab.videogames.games;

import be.vdab.videogames.consoles.Console;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String developer;
    private LocalDate releaseDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genre genre;

    @ManyToMany
    @JoinTable(
            name = "consolesgames",
            joinColumns = @JoinColumn(name = "gameId"),
            inverseJoinColumns = @JoinColumn(name = "consoleId"))
    @OrderBy("name")
    private Set<Console> consoles;

    public Game(String title, String developer, LocalDate releaseDate, Genre genre, Set<Console> consoles) {
        this.title = title;
        this.developer = developer;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.consoles = new LinkedHashSet<>(consoles);
    }

    protected Game() {
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDeveloper() {
        return developer;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public Genre getGenre() {
        return genre;
    }

    public Set<Console> getConsoles() {
        return Collections.unmodifiableSet(consoles);
    }

    public void addConsole(Console console) {
        consoles.add(console);
    }

    public void removeConsole(Console console) {
        consoles.remove(console);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Game game)) return false;
        return Objects.equals(getTitle(), game.getTitle()); //unieke constraint in db.
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getTitle());
    }
}

