package be.vdab.videogames.consoles;


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
        if (consoleRepository.existsByName(nieuweConsole.name())) {
            throw new ConsoleBestaatAlException();
        } // ok bij slechts één admin; geen race conditions.
        int huidigJaar = Year.now().getValue(); // haalt het huidige jaar op.
        if (nieuweConsole.releaseYear() > huidigJaar) {
            throw new ReleaseJaarInToekomstException();
        }
        Console console = new Console(
                nieuweConsole.name(),
                nieuweConsole.manufacturer(),
                nieuweConsole.releaseYear()
        );
        return consoleRepository.save(console);
    }

    @Transactional
    void delete(Long id) {
        consoleRepository.deleteById(id);
    }

    //wijzigingslogica kan eventueel later nog --> dan moet optimistic locking samen met een versie-kolom etc.
    //kan ik misschien in deze beperkte scope houden op: delete en dan nieuw maken.
}