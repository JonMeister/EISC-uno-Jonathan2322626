package org.example.eiscuno.model.game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.model.unoenum.EISCUnoEnum;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameUnoTest {

    private GameUno gameUno;
    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;

    public static class TestApp extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            // No need to show anything
        }
    }

    @BeforeAll
    public static void initJFX() throws Exception {
        new Thread(() -> Application.launch(TestApp.class)).start();
        Thread.sleep(5000); // Wait for the JavaFX platform to initialize
    }

    @BeforeEach
    public void setUp() {
        Platform.runLater(() -> {
            humanPlayer = new Player("HUMAN_PLAYER");
            machinePlayer = new Player("MACHINE_PLAYER");
            deck = new Deck();
            table = new Table();
            gameUno = new GameUno(humanPlayer, machinePlayer, deck, table);

            // Start the game and set initial state
            gameUno.startGame();
        });
    }

    @Test
    public void testEatCardPlusTwo() {
        Platform.runLater(() -> {
            // Create a +2 card
            Card plusTwoCard = new Card(EISCUnoEnum.TWO_WILD_DRAW_BLUE.getFilePath(), "+2", "BLUE");

            // Play the +2 card
            gameUno.playCard(plusTwoCard);
            gameUno.handleSpecialCards(plusTwoCard, humanPlayer);

            // Check that the machine player ate 2 cards
            assertEquals(7, machinePlayer.getCardsPlayer().size(), "Machine player debería tener 7 cartas después de comer 2 cartas de una carta +2.");
        });
    }

    @Test
    public void testEatCardPlusFour() {
        Platform.runLater(() -> {
            // Create a +4 card
            Card plusFourCard = new Card(EISCUnoEnum.WILD.getFilePath(), "+4", "NON_COLOR");

            // Play the +4 card
            gameUno.playCard(plusFourCard);
            gameUno.handleSpecialCards(plusFourCard, humanPlayer);

            // Check that the machine player ate 4 cards
            assertEquals(9, machinePlayer.getCardsPlayer().size(), "Machine player debería tener 9 cartas después de comer 4 cartas de una carta +4.");
        });
    }
}
