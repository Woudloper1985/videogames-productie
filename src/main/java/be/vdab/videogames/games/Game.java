package be.vdab.videogames.games;

import be.vdab.videogames.consoles.Console;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String developer;
    private LocalDate releaseDate;
    @ElementCollection(targetClass = Genre.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "gamegenres", joinColumns = @JoinColumn(name = "gameId"))
    @Column(name = "genre")
    private Set<Genre> genres = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(
            name = "consolesgames",
            joinColumns = @JoinColumn(name = "gameId"),
            inverseJoinColumns = @JoinColumn(name = "consoleId"))
    private Set<Console> consoles;

    public Game(String title, String developer, LocalDate releaseDate, Set<Genre> genres, Set<Console> consoles) {
        this.title = title;
        this.developer = developer;
        this.releaseDate = releaseDate;
        this.genres = new LinkedHashSet<>(genres);
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

    public Set<Genre> getGenres() {
        return Collections.unmodifiableSet(genres);
    }

    public Set<Console> getConsoles() {
        return Collections.unmodifiableSet(consoles);
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void removeGenre(Genre genre) {
        genres.remove(genre);
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
        return Objects.equals(getTitle(), game.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getTitle());
    }
}

