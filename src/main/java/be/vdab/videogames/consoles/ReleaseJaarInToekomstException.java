package be.vdab.videogames.consoles;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReleaseJaarInToekomstException extends RuntimeException {
    public ReleaseJaarInToekomstException() {
        super("Release-jaar mag niet in de toekomst liggen.");
    }
}