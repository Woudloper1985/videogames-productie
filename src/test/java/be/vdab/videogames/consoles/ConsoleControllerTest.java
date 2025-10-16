package be.vdab.videogames.consoles;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Sql("/consolesEnGames.sql")
@AutoConfigureMockMvc
// run de volledige klasse voor de tests OF voeg per method de environment variabelen toe in configs - dit is een probleem eigen aan IntelliJ.
class ConsoleControllerTest {

    private final static String CONSOLES_TABLE = "consoles";
    private final MockMvcTester mockMvcTester;
    private final JdbcClient jdbcClient;

    ConsoleControllerTest(MockMvcTester mockMvcTester, JdbcClient jdbcClient) {
        this.mockMvcTester = mockMvcTester;
        this.jdbcClient = jdbcClient;
    }

    private int idVanTest1Console() {
        return jdbcClient.sql("select id from consoles where name = 'TestConsole 1'")
                .query(Integer.class)
                .single();
    }

    @Test
    void findByIdMetBestaandeIdVindtJuisteConsoleMetJuisteGames() {
        var id = idVanTest1Console();
        var response = mockMvcTester.get()
                .uri("/consoles/{id}", id);
        assertThat(response)
                .hasStatusOk()
                .bodyJson()
                .satisfies(
                        json -> assertThat(json)
                                .extractingPath("name")
                                .isEqualTo("TestConsole 1"),
                        json -> assertThat(json)
                                .extractingPath("$.games")
                                .asArray()
                                .hasSize(2),
                        json -> assertThat(json)
                                .extractingPath("$.games[0].title")
                                .isEqualTo("TestGame 1"));
    }

    @Test
    void findByIdMetOnbestaandeIdGeeft404NotFound() {
        var response = mockMvcTester.get()
                .uri("/consoles/{id}", Long.MAX_VALUE);
        assertThat(response).hasStatus(404);
    }

    @Test
    void createMetCorrecteInvoerVoegtConsoleToe() {
    }

    @Test
    void createMetFouteInvoerGeeft400BadRequest() {
    }
}

// wordt getest in GameController --> zelfde methods:
//addGameToConsole()
//removeGameFromConsole()