package be.vdab.videogames.games;

import be.vdab.videogames.consoles.Console;
import be.vdab.videogames.consoles.ConsoleNietGevondenException;
import be.vdab.videogames.consoles.ConsoleRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
//vergeet niet @Transactional te typen voor CUD methods.
class GameService {
    private final GameRepository gameRepository;
    private final ConsoleRepository consoleRepository;

    GameService(GameRepository gameRepository, ConsoleRepository consoleRepository) {
        this.gameRepository = gameRepository;
        this.consoleRepository = consoleRepository;
    }

    //voor overzichtslijst in frontend.
    List<Game> findAll() {
        return gameRepository.findAll(Sort.by("title"));
    }

    Game findById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(GameNietGevondenException::new);
    }

    //voor zoekbalk in frontend.
    List<Game> findByTitleContainingIgnoreCase(String title) {
        return gameRepository.findByTitleContainingIgnoreCase(title);
    }

    List<Game> findByConsoleId(long consoleId) {
        consoleRepository.findById(consoleId)
                .orElseThrow(ConsoleNietGevondenException::new);
        return gameRepository.findByConsolesId(consoleId);
    }

    List<Genre> findAllGenres = Stream.of(Genre.values())
            .sorted(Comparator.comparing(Enum::name))
            .toList();

    List<Game> findGamesByGenre(Genre genre) {
        return gameRepository.findByGenresContaining(genre);
    }

    //...

    @Transactional
    void addGenreToGame(long gameId, Genre genre) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(GameNietGevondenException::new);
        game.addGenre(genre);
    }

    @Transactional
    void removeGenreFromGame(long gameId, Genre genre) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(GameNietGevondenException::new);
        game.removeGenre(genre);
    }

    @Transactional
    void addConsoleToGame(long gameId, long consoleId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(GameNietGevondenException::new);
        var console = consoleRepository.findById(consoleId)
                .orElseThrow(ConsoleNietGevondenException::new);
        game.addConsole(console);
    }

    @Transactional
    void removeConsoleFromGame(long gameId, long consoleId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(GameNietGevondenException::new);
        var console = consoleRepository.findById(consoleId)
                .orElseThrow(ConsoleNietGevondenException::new);
        game.removeConsole(console);
    }

    @Transactional
    public Game create(NieuweGame nieuweGame) {
        // Consoles ophalen via IDs
        Set<Console> consoles = nieuweGame.consoleIds().stream()
                .map(id -> consoleRepository.findById(id)
                        .orElseThrow(ConsoleNietGevondenException::new))
                .collect(Collectors.toSet());

        // Game entity aanmaken
        Game game = new Game(
                nieuweGame.title(),
                nieuweGame.developer(),
                nieuweGame.releaseDate(),
                nieuweGame.genres(),
                consoles
        );

        // Opslaan in DB
        return gameRepository.save(game);
    }


    @Transactional
    void delete(Long id) {
        gameRepository.deleteById(id);
    }

    //wijzigingslogica kan eventueel later nog --> dan moet optimistic locking samen met een versie-kolom etc.
    //kan ik misschien in deze beperkte scope houden op: delete en dan nieuw maken.
}
