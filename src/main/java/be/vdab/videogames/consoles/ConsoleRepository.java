package be.vdab.videogames.consoles;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsoleRepository extends JpaRepository<Console, Long> {

    @Override
    @EntityGraph(attributePaths = "games")
    Optional<Console> findById(Long id);

    boolean existsByName(String name);
}

