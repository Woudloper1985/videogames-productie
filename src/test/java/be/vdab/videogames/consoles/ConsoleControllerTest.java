package be.vdab.videogames.consoles;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

// onderstaande tests zijn niet exhaustief + focus op non-triviale tests.

@SpringBootTest
@Transactional
@Sql("/consolesEnGames.sql")
@AutoConfigureMockMvc
class ConsoleControllerTest {

    private final MockMvcTester mockMvcTester;
    private final JdbcClient jdbcClient;

    ConsoleControllerTest(MockMvcTester mockMvcTester, JdbcClient jdbcClient) {
        this.mockMvcTester = mockMvcTester;
        this.jdbcClient = jdbcClient;
    }

    @Test
    void findByIdMetBestaandeIdVindtJuisteConsoleMetJuisteGames() {
        var id = idVanTestConsole1();
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
        var id = idVanTestConsole2();
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

    @Autowired
    private EntityManager entityManager; // nodig hieronder, om na de PUT de staat van de DB te flushen naar de DB zelf.

    @Test
    void addGameHappyFlow() throws InterruptedException {
        var gameId = idVanTestGame3();
        var consoleId = idVanTestConsole2();

        var response = mockMvcTester.put()
                .uri("/consoles/{consoleId}/addGame/{gameId}", consoleId, gameId);

        assertThat(response).hasStatusOk();
        entityManager.flush();

        // Controleer dat er een record in de tussentabel is bijgekomen:
        var count = JdbcTestUtils.countRowsInTableWhere(jdbcClient, "consolesgames",
                "gameId=" + gameId + " and consoleId=" + consoleId);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void removeGameHappyFlow() {
        var gameId = idVanTestGame2();
        var consoleId = idVanTestConsole3();

        var response = mockMvcTester.delete()
                .uri("/consoles/{consoleId}/removeGame/{gameId}", consoleId, gameId);

        assertThat(response).hasStatusOk();
        entityManager.flush();

        // Controleer dat het record uit de tussentabel verdwenen is:
        var count = JdbcTestUtils.countRowsInTableWhere(jdbcClient, "consolesgames",
                "gameId=" + gameId + " and consoleId=" + consoleId);
        assertThat(count).isZero();
    }

    //HELPERS:

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

    private long idVanTestConsole1() { //console met 2 games
        return jdbcClient.sql("select id from consoles where name = 'TestConsole 1'")
                .query(Long.class)
                .single();
    }

    private long idVanTestConsole2() { //console zonder games
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