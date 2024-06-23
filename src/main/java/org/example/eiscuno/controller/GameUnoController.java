package org.example.eiscuno.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.game.IGameEndObserver;
import org.example.eiscuno.model.machine.IMachineObserver;
import org.example.eiscuno.model.machine.ThreadPlayMachine;
import org.example.eiscuno.model.machine.ThreadSingUNOMachine;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.model.unoenum.EISCUnoEnum;

/**
 * Controller for the GameUno application.
 * Manages the interactions between the user interface and the game logic.
 */
public class GameUnoController implements IMachineObserver, IGameEndObserver {

    @FXML
    private GridPane gridPaneCardsMachine;

    @FXML
    private GridPane gridPaneCardsPlayer;
    @FXML
    private BorderPane mainScene;
    @FXML
    private Label unoIcon;

    @FXML
    private ImageView tableImageView;
    @FXML
    private Button takeCardButton;
    @FXML
    private Button passTurnButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button unoButton;
    @FXML
    private Button attackUnoButton;

    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;
    private GameUno gameUno;
    private int posInitCardToShow;

    private ThreadSingUNOMachine threadSingUNOMachine;
    private ThreadPlayMachine threadPlayMachine;

    private volatile boolean humanPlayerCanSingUNO = true;
    private volatile boolean machineCanSingUNO = true;

    /**
     * Initializes the controller.
     * Sets up the game and starts the necessary threads.
     */
    @FXML
    public void initialize() {
        initVariables();
        this.gameUno.startGame();
        this.gameUno.addGameEndObserver(this); // Add game end observer
        updateTableImageView();
        printCardsHumanPlayer();
        printCardsMachinePlayer(); // Initialize machine player's cards

        threadSingUNOMachine = new ThreadSingUNOMachine(this.humanPlayer.getCardsPlayer(), this::machineCallsUNO);
        Thread t = new Thread(threadSingUNOMachine, "ThreadSingUNO");
        t.start();
        threadPlayMachine = new ThreadPlayMachine(this.table, this.machinePlayer, this.tableImageView, this.gameUno, this::disablePlayerCards, this::enablePlayerCards);
        threadPlayMachine.attach(this); // Subscribe to thread notifications
        threadPlayMachine.start();

        // Add images to buttons
        addImages();
    }

    /**
     * Initializes the game variables.
     */
    private void initVariables() {
        this.humanPlayer = new Player("HUMAN_PLAYER");
        this.machinePlayer = new Player("MACHINE_PLAYER");
        this.deck = new Deck();
        this.table = new Table();
        this.gameUno = new GameUno(this.humanPlayer, this.machinePlayer, this.deck, this.table);
        this.posInitCardToShow = 0;
        takeCardButton.setDisable(false); // Enable the button at the start of the game
        passTurnButton.setDisable(true);
        unoButton.setDisable(true);
        attackUnoButton.setDisable(true);
    }

    /**
     * Adds images to the buttons.
     */
    private void addImages() {
        // Create ImageView for takeCardButton
        ImageView takeCardImageView = new ImageView(new Image(getClass().getResource(EISCUnoEnum.DECK_OF_CARDS.getFilePath()).toString()));
        takeCardImageView.setFitWidth(100); // Set the desired width
        takeCardImageView.setFitHeight(110); // Set the desired height
        takeCardButton.setGraphic(takeCardImageView);

        // Create ImageView for unoButton
        ImageView unoImageView = new ImageView(new Image(getClass().getResource(EISCUnoEnum.BUTTON_UNO.getFilePath()).toString()));
        unoImageView.setFitWidth(50); // Set the desired width
        unoImageView.setFitHeight(50); // Set the desired height
        unoButton.setGraphic(unoImageView);

        // Create ImageView for attackUnoButton
        ImageView attackUnoImageView = new ImageView(new Image(getClass().getResource(EISCUnoEnum.BUTTON_UNO.getFilePath()).toString()));
        attackUnoImageView.setFitWidth(50); // Set the desired width
        attackUnoImageView.setFitHeight(50); // Set the desired height
        attackUnoButton.setGraphic(attackUnoImageView);

        // Create ImageView for exitButton
        ImageView exitImageView = new ImageView(new Image(getClass().getResource(EISCUnoEnum.EXIT.getFilePath()).toString()));
        exitImageView.setFitWidth(70); // Set the desired width
        exitImageView.setFitHeight(40); // Set the desired height
        exitButton.setGraphic(exitImageView);

        // Load the background image
        Image backgroundImage = new Image(getClass().getResource(EISCUnoEnum.BACKGROUND_UNO.getFilePath()).toString());

        // Create a BackgroundImage
        BackgroundImage background = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, true));

        // Set the background in the BorderPane
        mainScene.setBackground(new Background(background));

        // Load the icon image
        Image iconImage = new Image(getClass().getResource(EISCUnoEnum.UNO.getFilePath()).toString());

        // Create an ImageView and set the size if necessary
        ImageView iconImageView = new ImageView(iconImage);
        iconImageView.setFitWidth(100); // Adjust the size as needed
        iconImageView.setFitHeight(100); // Adjust the size as needed

        // Set the ImageView as the graphic of the Label
        unoIcon.setGraphic(iconImageView);
    }

    /**
     * Prints the human player's cards to the grid pane.
     */
    private void printCardsHumanPlayer() {
        this.gridPaneCardsPlayer.getChildren().clear();
        Card[] currentVisibleCardsHumanPlayer = this.gameUno.getCurrentVisibleCardsHumanPlayer(this.posInitCardToShow);

        for (int i = 0; i < currentVisibleCardsHumanPlayer.length; i++) {
            Card card = currentVisibleCardsHumanPlayer[i];
            ImageView cardImageView = card.getCard();

            cardImageView.setOnMouseClicked((MouseEvent event) -> {
                Card topCard = table.getCurrentCardOnTheTable();
                if (gameUno.isCardPlayable(card, topCard)) {
                    gameUno.playCard(card);
                    if (!gameUno.handleSpecialCards(card, humanPlayer)) {
                        threadPlayMachine.setHasPlayerPlayed(true);
                    }
                    tableImageView.setImage(card.getImage());
                    humanPlayer.removeCard(findPosCardsHumanPlayer(card));
                    printCardsHumanPlayer();
                    printCardsMachinePlayer();
                    if (humanPlayer.getCardsPlayer().size() == 1) {
                        unoButton.setDisable(false);
                        humanPlayerCanSingUNO = true;
                    }
                } else {
                    // Mostrar un mensaje o alerta indicando que la carta no es válida
                    System.out.println("Carta no válida. Debes jugar una carta del mismo color o número.");
                }
            });

            this.gridPaneCardsPlayer.add(cardImageView, i, 0);
        }
    }

    /**
     * Prints the machine player's cards to the grid pane.
     */
    public void printCardsMachinePlayer() {
        gridPaneCardsMachine.getChildren().clear();
        Image backCardImage = new Image(getClass().getResource(EISCUnoEnum.CARD_UNO.getFilePath()).toString());

        for (int i = 0; i < machinePlayer.getCardsPlayer().size() && i < 4; i++) {
            ImageView cardImageView = new ImageView(backCardImage);
            cardImageView.setFitHeight(90);
            cardImageView.setFitWidth(70);
            gridPaneCardsMachine.add(cardImageView, i, 0);

            cardImageView.setUserData(machinePlayer.getCardsPlayer().get(i));
        }
        if (machinePlayer.getCardsPlayer().size() == 1) {
            attackUnoButton.setDisable(false);
            new Thread(() -> {
                try {
                    Thread.sleep(2000 + (long) (Math.random() * 3000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    machineCanSingUNO = true;
                    attackUnoButton.setDisable(true);
                });
            }).start();
        } else {
            attackUnoButton.setDisable(true);
        }
    }

    /**
     * Finds the position of a card in the human player's hand.
     *
     * @param card the card to find
     * @return the position of the card, or -1 if not found
     */
    private Integer findPosCardsHumanPlayer(Card card) {
        for (int i = 0; i < this.humanPlayer.getCardsPlayer().size(); i++) {
            if (this.humanPlayer.getCardsPlayer().get(i).equals(card)) {
                return i;
            }
        }
        return -1;
    }

    @FXML
    void onHandleBack(ActionEvent event) {
        if (this.posInitCardToShow > 0) {
            this.posInitCardToShow--;
            printCardsHumanPlayer();
        }
    }

    @FXML
    void onHandleNext(ActionEvent event) {
        if (this.posInitCardToShow < this.humanPlayer.getCardsPlayer().size() - 4) {
            this.posInitCardToShow++;
            printCardsHumanPlayer();
        }
    }

    @FXML
    void onHandleTakeCard(ActionEvent event) {
        // Implement logic to take a card here
        gameUno.eatCard(humanPlayer, 1);
        printCardsHumanPlayer();
        takeCardButton.setDisable(true); // Disable the button after taking a card
        passTurnButton.setDisable(false);
    }

    @FXML
    void onHandleUno(ActionEvent event) {
        // Implement logic to handle Uno event here
        if (humanPlayer.getCardsPlayer().size() == 1 && humanPlayerCanSingUNO) {
            humanPlayerCanSingUNO = false;
            unoButton.setDisable(true);
        } else {
            unoButton.setDisable(true);
            printCardsHumanPlayer();
        }
    }

    @FXML
    void onHandleAttackUno(ActionEvent event) {
        humanCallsUNO();
    }

    @FXML
    void onHandlePassTurn(ActionEvent event) {
        threadPlayMachine.setHasPlayerPlayed(true); // Pass the turn to the machine
        takeCardButton.setDisable(true); // Disable the take card button
        passTurnButton.setDisable(true); // Disable the pass turn button
    }

    @FXML
    void onHandleExit(ActionEvent event) {
        // Stop the machine and sing UNO threads
        if (threadSingUNOMachine != null && threadSingUNOMachine.isRunning()) {
            threadSingUNOMachine.stopRunning();
        }
        if (threadPlayMachine != null && threadPlayMachine.isRunning()) {
            threadPlayMachine.stopRunning();
        }

        // Close the current window
        Platform.exit();
        System.exit(0);
    }

    @Override
    public void updateMachineView() {
        Platform.runLater(this::printCardsMachinePlayer);
        Platform.runLater(this::printCardsHumanPlayer);
    }

    /**
     * Updates the table image view with the top card on the table.
     */
    private void updateTableImageView() {
        Card topCard = table.getCurrentCardOnTheTable();
        if (topCard != null) {
            tableImageView.setImage(topCard.getImage());
        }
    }

    /**
     * Disables the player's cards.
     */
    private void disablePlayerCards() {
        for (Card card : humanPlayer.getCardsPlayer()) {
            card.getCard().setDisable(true);
        }
        takeCardButton.setDisable(true); // Disable the take card button
        passTurnButton.setDisable(true);
    }

    /**
     * Enables the player's cards.
     */
    private void enablePlayerCards() {
        for (Card card : humanPlayer.getCardsPlayer()) {
            card.getCard().setDisable(false);
        }
        takeCardButton.setDisable(false); // Enable the take card button
        passTurnButton.setDisable(false);
    }

    /**
     * Handles the machine calling UNO.
     */
    private void machineCallsUNO() {
        Platform.runLater(() -> {
            if (humanPlayer.getCardsPlayer().size() == 1 && humanPlayerCanSingUNO) {
                gameUno.eatCard(humanPlayer, 1);
                printCardsHumanPlayer();
            }
        });
    }

    /**
     * Handles the human player calling UNO.
     */
    private void humanCallsUNO() {
        Platform.runLater(() -> {
            if (machinePlayer.getCardsPlayer().size() == 1) {
                gameUno.eatCard(machinePlayer, 1);
                printCardsMachinePlayer();
            }
        });
    }

    @Override
    public void onGameEnd(String winner) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fin del Juego");
            alert.setHeaderText(null);
            alert.setContentText("El juego ha terminado. El ganador es: " + winner);
            alert.showAndWait();
            unoButton.setDisable(true);
            passTurnButton.setDisable(true);
            takeCardButton.setDisable(true);
            attackUnoButton.setDisable(true);
            disablePlayerCards();
        });
    }
}
