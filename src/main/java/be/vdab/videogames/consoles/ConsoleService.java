package be.vdab.videogames.consoles;


import be.vdab.videogames.games.Game;
import be.vdab.videogames.games.GameNietGevondenException;
import be.vdab.videogames.games.GameRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
class ConsoleService {
    private final ConsoleRepository consoleRepository;
    private final GameRepository gameRepository;

    ConsoleService(ConsoleRepository consoleRepository, GameRepository gameRepository) {
        this.consoleRepository = consoleRepository;
        this.gameRepository = gameRepository;
    }

    //voor overzichtslijst in frontend.
    List<Console> findAll() {
        return consoleRepository.findAll(Sort.by("name"));
    }

    Console findById(Long id) {
        return consoleRepository.findById(id)
                .orElseThrow(ConsoleNietGevondenException::new);
    }

    @Transactional
    Console create(Console console) { // zal DTO NieuweConsole met validatielogica moeten worden
        return consoleRepository.save(console);
    }

    @Transactional
    void delete(Long id) {
        consoleRepository.deleteById(id);
    }

    //wijzigingslogica kan eventueel later nog --> dan moet optimistic locking samen met een versie-kolom etc.
    //kan ik misschien in deze beperkte scope houden op: delete en dan nieuw maken.

    @Transactional
    void addGameToConsole(long consoleId, long gameId) {
        Console console = consoleRepository.findById(consoleId)
                .orElseThrow(ConsoleNietGevondenException::new);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(GameNietGevondenException::new);
        console.addGame(game);
    }

    @Transactional
    void removeGameFromConsole(long consoleId, long gameId) {
        Console console = consoleRepository.findById(consoleId)
                .orElseThrow(ConsoleNietGevondenException::new);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(GameNietGevondenException::new);
        console.removeGame(game);
    }


}