package be.vdab.videogames.games;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {

//    @Override
//    @EntityGraph(attributePaths = "genres")
//    List<Game> findAll();

    @Override
    @EntityGraph(attributePaths = "genres")
    Optional<Game> findById(Long id);

    List<Game> findByGenresContaining(Genre genre);

    List<Game> findByTitleContainingIgnoreCase(String title);

    List<Game> findByConsolesId(Long consoleId);
}