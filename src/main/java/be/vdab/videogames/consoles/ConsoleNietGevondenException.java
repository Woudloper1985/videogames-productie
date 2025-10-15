package be.vdab.videogames.consoles;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ConsoleNietGevondenException extends RuntimeException {
    public ConsoleNietGevondenException() {
        super("Console niet gevonden.");
    }
}
