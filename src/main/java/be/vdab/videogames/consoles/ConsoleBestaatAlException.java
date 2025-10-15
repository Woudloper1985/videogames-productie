package be.vdab.videogames.consoles;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
class ConsoleBestaatAlException extends RuntimeException {
    public ConsoleBestaatAlException() {
        super("Console bestaat al.");
    }
}
