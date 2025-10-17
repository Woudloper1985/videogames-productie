package be.vdab.videogames.games;

import be.vdab.videogames.consoles.Console;
import be.vdab.videogames.consoles.ConsoleNietGevondenException;
import be.vdab.videogames.consoles.ConsoleRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public//vergeet niet @Transactional te typen voor CUD methods.
class GameService {
    private final GameRepository gameRepository;
    private final ConsoleRepository consoleRepository;

    GameService(GameRepository gameRepository, ConsoleRepository consoleRepository) {
        this.gameRepository = gameRepository;
        this.consoleRepository = consoleRepository;
    }

    long findAantal() {
        return gameRepository.count();
    }

    //voor overzichtslijst in frontend.
    List<Game> findAll() {
        return gameRepository.findAll(Sort.by("title"));
    }

    //moet misschien met optional en dan in de controller exception?
    Optional<Game> findById(Long id) {
        return gameRepository.findById(id);
    }

    //voor zoekbalk in frontend.
    List<Game> findByTitleContainingIgnoreCase(String title) {
        return gameRepository.findByTitleContainingIgnoreCase(title, Sort.by(Sort.Order.by("title").ignoreCase()));
    }

    List<Game> findGamesByGenre(Genre genre) {
        return gameRepository.findByGenre(genre, Sort.by(Sort.Order.by("title").ignoreCase()));
    }

    List<Game> findByConsoleId(long consoleId) {
//        consoleRepository.findById(consoleId)
//                .orElseThrow(ConsoleNietGevondenException::new); ZAL ALLICHT NIET NODIG ZIJN, WANT FRONTEND ZAL EERST FindById doen uit ConsoleController.
        return gameRepository.findByConsolesId(consoleId);
    }

    //...

    @Transactional
    Game create(NieuweGame nieuweGame) {
        if (gameRepository.existsByTitle(nieuweGame.title())) {
            throw new GameBestaatAlException();
        } // ok bij slechts één admin; geen race conditions.

        // Alle consoles in één query ophalen
        var consoles = new LinkedHashSet<>(consoleRepository.findAllById(nieuweGame.consoleIds()));

        // Controleer of alle IDs bestaan
        if (consoles.size() != nieuweGame.consoleIds().size()) {
            throw new ConsoleNietGevondenException();
        }
        // Game entity aanmaken
        Game game = new Game(
                nieuweGame.title(),
                nieuweGame.developer(),
                nieuweGame.releaseDate(),
                nieuweGame.genre(),
                consoles
        );
        // Opslaan in DB
        return gameRepository.save(game);
    }

    @Transactional
    void delete(Long id) {
        gameRepository.deleteById(id);
    }

    @Transactional
    public void addConsoleToGame(long gameId, long consoleId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(GameNietGevondenException::new);
        var console = consoleRepository.findById(consoleId)
                .orElseThrow(ConsoleNietGevondenException::new);
        game.addConsole(console);
    }

    @Transactional
    public void removeConsoleFromGame(long gameId, long consoleId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(GameNietGevondenException::new);
        var console = consoleRepository.findById(consoleId)
                .orElseThrow(ConsoleNietGevondenException::new);
        game.removeConsole(console);
    }

    //wijzigingslogica kan eventueel later nog --> dan moet optimistic locking samen met een versie-kolom etc.
    //kan ik misschien in deze beperkte scope houden op: delete en dan nieuw maken.
}
