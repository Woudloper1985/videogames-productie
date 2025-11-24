package be.vdab.videogames.games;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface GameRepository extends JpaRepository<Game, Long> {

    @Override
    @EntityGraph(attributePaths = "consoles")
    Optional<Game> findById(Long id);

    @EntityGraph(attributePaths = "consoles")
    List<Game> findByTitleContainingIgnoreCase(String title, Sort by);

    @EntityGraph(attributePaths = "consoles")
    List<Game> findByGenre(Genre genre, Sort by);

    boolean existsByTitle(String title);
}