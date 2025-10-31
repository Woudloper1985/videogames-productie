package be.vdab.videogames.games;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("games")
@CrossOrigin
class GameController {
    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("aantal")
    long findAantal() {
        return gameService.findAantal();
    }

    //kan eventueel ook nog een findAantalByGenre(Genre genre) gemaakt worden.

    @GetMapping
    Stream<GameBeknopt> findAll() {
        return gameService.findAll()
                .stream()
                .map(GameBeknopt::new);
    }

    @GetMapping("{id}")
    GameMetConsoles findById(@PathVariable long id) {
        return gameService.findById(id)
                .map(GameMetConsoles::new)
                .orElseThrow(GameNietGevondenException::new);
    }

    // lege invoer (400) in frontend afvangen, evenals niets gevonden (200 met lege lijst)
    @GetMapping(params = "title")
    List<GameBeknopt> findByTitleContainingIgnoreCase(@NotBlank String title) {
        return gameService.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(GameBeknopt::new)
                .toList();
    }

    @GetMapping("genres")
    List<Genre> findAllGenres() {
        return Genre.sortedValues();
    }

    @GetMapping("genre/{genre}")
    List<GameBeknopt> findByGenre(@PathVariable Genre genre) {
        return gameService.findGamesByGenre(genre)
                .stream()
                .map(GameBeknopt::new)
                .toList();
    }

    //overbodige method:

//    @GetMapping("/opConsole/{consoleId}")
//    List<GameBeknopt> findByConsoleId(@PathVariable long consoleId) {
//        return gameService.findByConsoleId(consoleId)
//                .stream()
//                .map(GameBeknopt::new)
//                .toList();
//    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Game create(@RequestBody @Valid NieuweGame nieuweGame) {
        return gameService.create(nieuweGame);
    }

    @DeleteMapping("{id}")
    void delete(@PathVariable long id) {
        try {
            gameService.delete(id);
        } catch (EmptyResultDataAccessException ignored) {
        }
    }

    // mag weg, is identieke method voor game.js en console.js

    @PutMapping("{gameId}/addConsole/{consoleId}")
    void addConsoleToGame(@PathVariable long gameId, @PathVariable long consoleId) {
        gameService.addConsoleToGame(gameId, consoleId);
    }

    @DeleteMapping("{gameId}/removeConsole/{consoleId}")
    void removeConsoleFromGame(@PathVariable long gameId, @PathVariable long consoleId) {
        gameService.removeConsoleFromGame(gameId, consoleId);
    }
}