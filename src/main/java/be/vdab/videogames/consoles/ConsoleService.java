package be.vdab.videogames.consoles;

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

    //voor overzichtslijst in frontend:
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
}