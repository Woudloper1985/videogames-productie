package be.vdab.videogames.consoles;

import be.vdab.videogames.games.Game;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
class ConsoleService {
    private final ConsoleRepository consoleRepository;

    ConsoleService(ConsoleRepository consoleRepository) {
        this.consoleRepository = consoleRepository;
    }

    //voor overzichtslijst in frontend.
    List<Console> findAll() {
        return consoleRepository.findAll(Sort.by("name"));
    }

    Optional<Console> findById(Long id) {
        return consoleRepository.findById(id);
    }

    @Transactional
    Console create(NieuweConsole nieuweConsole) {
        int huidigJaar = Year.now().getValue();
        if (nieuweConsole.releaseYear() > huidigJaar) {
            throw new ReleaseInToekomstException(); // 400 Bad Request
        }

        try {
            Console console = new Console(
                    nieuweConsole.name(),
                    nieuweConsole.manufacturer(),
                    nieuweConsole.releaseYear()
            );
            return consoleRepository.save(console);
        } catch (DataIntegrityViolationException e) {
            // Unieke constraint violation → console bestaat al
            throw new ConsoleBestaatAlException(); // 409 Conflict
        }
    }

//    @Transactional
//    void delete(Long id) {
//        var console = consoleRepository.findById(id)
//                .orElseThrow(ConsoleNietGevondenException::new);
//        console.getGames().forEach(game -> game.removeConsole(console));
//        consoleRepository.deleteById(id);
//    }

    @Transactional
    public void delete(Long id) {
        Console console = consoleRepository.findById(id)
                .orElseThrow(ConsoleNietGevondenException::new);

        // koppelingen met games losmaken
        for (Game game : console.getGames()) {
            game.getConsoles().remove(console);
        }
        console.getGames().clear();
        consoleRepository.delete(console);
    }


    //wijzigingslogica kan eventueel later nog --> dan moet optimistic locking samen met een versie-kolom etc.
    // maar optimistic locking is niet strikt nodig voor één gebruiker...
    // kan ik misschien in deze beperkte scope houden op: delete en dan nieuw maken.

//    @Transactional
//    public void updateName(long id, String name) {
//        var console = consoleRepository.findById(id)
//                .orElseThrow(ConsoleNietGevondenException::new);
//        console.setName(name);
//    }
//
//    @Transactional
//    public void updateManufacturer(long id, String manufacturer) {
//        var console = consoleRepository.findById(id)
//                .orElseThrow(ConsoleNietGevondenException::new);
//        console.setManufacturer(manufacturer);
//    }
//
//    @Transactional
//    public void updateReleaseYear(long id, int year) {
//        var console = consoleRepository.findById(id)
//                .orElseThrow(ConsoleNietGevondenException::new);
//        console.setReleaseYear(year);
//    }
}