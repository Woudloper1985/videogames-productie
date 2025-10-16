package be.vdab.videogames.consoles;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

// onderstaande tests zijn niet exhaustief; focus op non-triviale tests.

@SpringBootTest
@Transactional
@Sql("/consolesEnGames.sql")
@AutoConfigureMockMvc
class ConsoleControllerTest {

    //private final static String CONSOLES_TABLE = "consoles";
    private final MockMvcTester mockMvcTester;
    private final JdbcClient jdbcClient;

    ConsoleControllerTest(MockMvcTester mockMvcTester, JdbcClient jdbcClient) {
        this.mockMvcTester = mockMvcTester;
        this.jdbcClient = jdbcClient;
    }

    private int idVanTest1Console() { //console met 2 games
        return jdbcClient.sql("select id from consoles where name = 'TestConsole 1'")
                .query(Integer.class)
                .single();
    }

    private int idVanTest2Console() { //console zonder games
        return jdbcClient.sql("select id from consoles where name = 'TestConsole 2'")
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
                                .extractingPath("$.name")
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
    void findByIdConsoleZonderGamesGeeftLegeGamesArray() {
        var id = idVanTest2Console();
        var response = mockMvcTester.get()
                .uri("/consoles/{id}", id);
        assertThat(response)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.games")
                .asArray()
                .isEmpty();
    }

    @Test
    void findByIdMetOnbestaandeIdGeeft404NotFound() {
        var response = mockMvcTester.get()
                .uri("/consoles/{id}", Long.MAX_VALUE);
        assertThat(response).hasStatus(404);
    }

    @Test
    void createMetCorrecteInvoerVoegtConsoleToe() throws Exception {
        var jsonData = new ClassPathResource("consoleCorrect.json")
                .getContentAsString(StandardCharsets.UTF_8);
        var response = mockMvcTester.post()
                .uri("/consoles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData);
        assertThat(response)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.name")
                .isEqualTo("NieuweConsole");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "legeNaamConsole.json",
            "legeManufacturerConsole.json",
            "teOudeConsole.json",
            "toekomstigeConsole.json"})
    void createMetFouteInvoerGeeft400BadRequest(String fouteConsole) throws Exception {
        var jsonData = new ClassPathResource(fouteConsole)
                .getContentAsString(StandardCharsets.UTF_8);
        var response = mockMvcTester.post()
                .uri("/consoles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData);
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST);
    }
}

// worden getest in GameController, want zelfde methods daar:
//addGameToConsole()
//removeGameFromConsole()