package be.vdab.videogames.games;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

// onderstaande tests zijn niet exhaustief + focus op non-triviale tests.

@SpringBootTest
@Transactional
@Sql("/consolesEnGames.sql")
@AutoConfigureMockMvc
class GameControllerTest {

    private final static String GAMES_TABLE = "games";
    private final MockMvcTester mockMvcTester;
    private final JdbcClient jdbcClient;

    GameControllerTest(MockMvcTester mockMvcTester, JdbcClient jdbcClient) {
        this.mockMvcTester = mockMvcTester;
        this.jdbcClient = jdbcClient;
    }

    @Test
    void findByIdMetBestaandeIdGeeftJuisteGameMetJuisteConsoles() {
        var id = idVanTestGame1();
        var response = mockMvcTester.get()
                .uri("/games/{id}", id);
        assertThat(response)
                .hasStatusOk()
                .bodyJson()
                .satisfies(
                        json -> assertThat(json)
                                .extractingPath("title")
                                .isEqualTo("TestGame 1"),
                        json -> assertThat(json)
                                .extractingPath("$.consoles")
                                .asArray()
                                .hasSize(2),
                        json -> assertThat(json)
                                .extractingPath("$.consoles[0].name")
                                .isEqualTo("TestConsole 1"));
    }

    @Test
    void findByIdMetOnbestaandeIdGeeft404() {
        var response = mockMvcTester.get()
                .uri("/games/{id}", Long.MAX_VALUE);
        assertThat(response).hasStatus(404);
    }

    @Test
    void findByTitleContainingIgnoreCaseGeeftJuisteGames() {
        var response = mockMvcTester.get()
                .uri("/games?title=TeStGA");
        assertThat(response)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.length()")
                .isEqualTo(3); //kan uitgebreid worden met satisfies om titels te checken.
    }

    @Test
    void findByGenreGeeftJuisteGames() {
        var response = mockMvcTester.get()
                .uri("/games/genre/SHOOTER");
        assertThat(response)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.length()")
                .isEqualTo(JdbcTestUtils.countRowsInTableWhere(jdbcClient, GAMES_TABLE, "genre = 'SHOOTER'"));
    }

    @Test
    void findByConsoleIdGeeftJuisteGames() {
        var response = mockMvcTester.get()
                .uri("/games/opConsole/{consoleId}", idVanTestConsole1());
        assertThat(response)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.length()")
                .isEqualTo(2);
    }

    @Test
    void createWerkt() {
        var jsonBody = """
                {
                    "title": "Nieuwe Game",
                    "developer": "Nieuwe Developer",
                    "releaseDate": "2023-01-01",
                    "genre": "RPG",
                    "consoleIds": [%d]
                }
                """.formatted(idVanTestConsole2()); // met meerdere consoles krijg ik malicious content error (heeft iets met JSON parsing te maken).
        var response = mockMvcTester.post()
                .uri("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody);
        assertThat(response)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .satisfies(
                        json -> assertThat(json)
                                .extractingPath("title")
                                .isEqualTo("Nieuwe Game"),
                        json -> assertThat(json)
                                .extractingPath("$.consoles")
                                .asArray()
                                .hasSize(1),
                        json -> assertThat(json)
                                .extractingPath("$.consoles[0].name")
                                .isEqualTo("TestConsole 2"));

    }

    @Autowired
    private EntityManager entityManager; // nodig hieronder, om na de PUT de staat van de DB te flushen naar de DB zelf.

    @Test
    void addConsoleHappyFlow() throws InterruptedException {
        var gameId = idVanTestGame3();
        var consoleId = idVanTestConsole2();

        var response = mockMvcTester.put()
                .uri("/games/{gameId}/addConsole/{consoleId}", gameId, consoleId);

        assertThat(response).hasStatusOk();
        entityManager.flush();

        // Controleer dat er een record in de tussentabel is gekomen:
        var count = JdbcTestUtils.countRowsInTableWhere(jdbcClient, "consolesgames",
                "gameId=" + gameId + " and consoleId=" + consoleId);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void removeConsoleHappyFlow() {
        var gameId = idVanTestGame2();
        var consoleId = idVanTestConsole3();

        var response = mockMvcTester.delete()
                .uri("/games/{gameId}/removeConsole/{consoleId}", gameId, consoleId);

        assertThat(response).hasStatusOk();
        entityManager.flush();

        // Controleer dat het record uit de tussentabel verdwenen is:
        var count = JdbcTestUtils.countRowsInTableWhere(jdbcClient, "consolesgames",
                "gameId=" + gameId + " and consoleId=" + consoleId);
        assertThat(count).isZero();
    }

    //HELPERS:

    private long idVanTestGame1() {
        return jdbcClient.sql("select id from games where title = 'TestGame 1'")
                .query(Long.class)
                .single();
    }

    private long idVanTestGame2() {
        return jdbcClient.sql("select id from games where title = 'TestGame 2'")
                .query(Long.class)
                .single();
    }

    private long idVanTestGame3() {
        return jdbcClient.sql("select id from games where title = 'TestGame 3'")
                .query(Long.class)
                .single();
    }

    private long idVanTestConsole1() {
        return jdbcClient.sql("select id from consoles where name = 'TestConsole 1'")
                .query(Long.class)
                .single();
    }

    private long idVanTestConsole2() {
        return jdbcClient.sql("select id from consoles where name = 'TestConsole 2'")
                .query(Long.class)
                .single();
    }

    private long idVanTestConsole3() {
        return jdbcClient.sql("select id from consoles where name = 'TestConsole 3'")
                .query(Long.class)
                .single();
    }
}