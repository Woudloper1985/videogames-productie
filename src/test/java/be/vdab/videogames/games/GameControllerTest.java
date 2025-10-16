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

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

// onderstaande tests zijn niet exhaustief; focus op non-triviale tests.

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

    private long idVanTestGame1() {
        return jdbcClient.sql("select id from games where title = 'TestGame 1'")
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
    void findByTitleContainingIgnoreCaseGeeftCorrecteGames() {
        var response = mockMvcTester.get()
                .uri("/games?title=TeStGA");
        assertThat(response)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.length()")
                .isEqualTo(3); //kan uitgebreid worden met satisfies om titels te checken.
    }

    @Test

    void findByGenreGeeftCorrecteGames() {
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
    void createGeeftCorrecteGame() {
            var nieuweGame = new NieuweGame(
                    "MijnTestGame",
                    "TestDev",
                    LocalDate.of(2023, 1, 1),
                    Set.of(idVanTestConsole1()), // minstens één console
                    Genre.ACTION
            );

            var response = mockMvcTester.post()
                    .uri("/games")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(String.valueOf(nieuweGame));

            assertThat(response)
                    .hasStatus(HttpStatus.CREATED)
                    .bodyJson()
                    .extractingPath("title")
                    .isEqualTo("MijnTestGame");
        }


    }
