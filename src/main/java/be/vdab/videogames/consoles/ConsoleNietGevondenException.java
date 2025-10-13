package be.vdab.videogames.consoles;

public class ConsoleNietGevondenException extends RuntimeException {
    public ConsoleNietGevondenException() {
        super("Console niet gevonden.");
    }
}
