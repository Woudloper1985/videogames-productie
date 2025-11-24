package be.vdab.videogames.consoles;

import be.vdab.videogames.games.GameService;
import jakarta.validation.Valid;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("consoles")
@CrossOrigin
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Console create(@RequestBody @Valid NieuweConsole nieuweConsole) {
        return consoleService.create(nieuweConsole);
    }

    // zelfde methods voor game.js en console.js in frontend:
    @PutMapping("{consoleId}/addGame/{gameId}")
    void addGameToConsole(@PathVariable long consoleId, @PathVariable long gameId) {
        gameService.addGameToConsole(consoleId, gameId);
    }

    @DeleteMapping("{consoleId}/removeGame/{gameId}")
    void removeGameFromConsole(@PathVariable long consoleId, @PathVariable long gameId) {
        gameService.removeGameFromConsole(consoleId, gameId);
    }
}