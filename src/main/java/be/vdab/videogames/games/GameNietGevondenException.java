package be.vdab.videogames.games;

public class GameNietGevondenException extends RuntimeException {
    public GameNietGevondenException() {
        super("Game niet gevonden.");
    }
}
