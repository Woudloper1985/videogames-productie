package be.vdab.videogames.games;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
class GameBestaatAlException extends RuntimeException {
    public GameBestaatAlException() {
        super("Game bestaat al.");
    }
}