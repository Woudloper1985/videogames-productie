package be.vdab.videogames.consoles;

import be.vdab.videogames.games.GameService;
import jakarta.validation.Valid;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("consoles")
class ConsoleController {
    private final ConsoleService consoleService;
    private final GameService gameService;

    ConsoleController(ConsoleService consoleService, GameService gameService) {
        this.consoleService = consoleService;
        this.gameService = gameService;
    }

    @GetMapping
    List<ConsoleBeknopt> findAll() {
        return consoleService.findAll()
                .stream()
                .map(ConsoleBeknopt::new)
                .toList();
    }

    @GetMapping("{id}")
    ConsoleMetGames findById(@PathVariable long id) {
        return consoleService.findById(id)
                .map(ConsoleMetGames::new)
                .orElseThrow(ConsoleNietGevondenException::new);
    }

    // in de frontend een 400-foutcode afvangen en een bericht tonen als de validatie faalt ("foute invoer"...)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Console create(@RequestBody @Valid NieuweConsole nieuweConsole) {
        return consoleService.create(nieuweConsole);
    }

    @DeleteMapping("{id}")
    void delete(@PathVariable long id) {
        try {
            consoleService.delete(id);
        } catch (EmptyResultDataAccessException ignored) {
        }
    }

    @PutMapping("{consoleId}/addGame/{gameId}")
    void addGameToConsole(@PathVariable long consoleId, @PathVariable long gameId) {
        gameService.addConsoleToGame(gameId, consoleId);
    }

    @DeleteMapping("{consoleId}/removeGame/{gameId}")
    void removeGameFromConsole(@PathVariable long consoleId, @PathVariable long gameId) {
        gameService.removeConsoleFromGame(gameId, consoleId);
    }
}