package be.vdab.videogames.games;

import org.junit.jupiter.api.Test;
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
                .isEqualTo(3); //kan uitgebreid worden met .satisfies om titels te checken.
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
    void createWerkt() {
        var jsonBody = """
                {
                    "title": "Nieuwe Game",
                    "developer": "Nieuwe Developer",
                    "releaseDate": "2023-01-01",
                    "genre": "RPG",
                    "consoleIds": [%d]
                }
                """.formatted(idVanTestConsole2());
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

    //HELPERS:

    private long idVanTestGame1() {
        return jdbcClient.sql("select id from games where title = 'TestGame 1'")
                .query(Long.class)
                .single();
    }

    private long idVanTestConsole2() {
        return jdbcClient.sql("select id from consoles where name = 'TestConsole 2'")
                .query(Long.class)
                .single();
    }
}