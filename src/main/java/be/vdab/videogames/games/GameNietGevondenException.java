package be.vdab.videogames.games;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
class GameNietGevondenException extends RuntimeException {
    public GameNietGevondenException() {
        super("Game niet gevonden.");
    }
}
