package be.vdab.videogames.consoles;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("consoles")
class ConsoleController {
    private final ConsoleService consoleService;

    ConsoleController(ConsoleService consoleService) {
        this.consoleService = consoleService;
    }

    @DeleteMapping("{id}")
    void delete(@PathVariable long id) {
        try {
            consoleService.delete(id);
        } catch (EmptyResultDataAccessException ignored) {
        }
    }
}
