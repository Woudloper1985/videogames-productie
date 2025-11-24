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
}