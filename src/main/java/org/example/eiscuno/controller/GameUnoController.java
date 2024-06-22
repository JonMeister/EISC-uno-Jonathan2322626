package org.example.eiscuno.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.machine.IMachineObserver;
import org.example.eiscuno.model.machine.ThreadPlayMachine;
import org.example.eiscuno.model.machine.ThreadSingUNOMachine;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

public class GameUnoController implements IMachineObserver {

    @FXML
    private GridPane gridPaneCardsMachine;

    @FXML
    private GridPane gridPaneCardsPlayer;

    @FXML
    private ImageView tableImageView;

    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;
    private GameUno gameUno;
    private int posInitCardToShow;

    private ThreadSingUNOMachine threadSingUNOMachine;
    private ThreadPlayMachine threadPlayMachine;

    @FXML
    public void initialize() {
        initVariables();
        this.gameUno.startGame();
        updateTableImageView();
        printCardsHumanPlayer();
        printCardsMachinePlayer(); // Inicializa las cartas de la máquina

        threadSingUNOMachine = new ThreadSingUNOMachine(this.humanPlayer.getCardsPlayer());
        Thread t = new Thread(threadSingUNOMachine, "ThreadSingUNO");
        t.start();
        threadPlayMachine = new ThreadPlayMachine(this.table, this.machinePlayer, this.tableImageView,this.gameUno,this::disablePlayerCards,this::enablePlayerCards);
        threadPlayMachine.attach(this); // Suscribirse a las notificaciones del hilo
        threadPlayMachine.start();
    }

    private void initVariables() {
        this.humanPlayer = new Player("HUMAN_PLAYER");
        this.machinePlayer = new Player("MACHINE_PLAYER");
        this.deck = new Deck();
        this.table = new Table();
        this.gameUno = new GameUno(this.humanPlayer, this.machinePlayer, this.deck, this.table);
        this.posInitCardToShow = 0;
    }

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
                    if(!gameUno.handleSpecialCards(card, humanPlayer)){
                        threadPlayMachine.setHasPlayerPlayed(true);
                    }
                    tableImageView.setImage(card.getImage());
                    humanPlayer.removeCard(findPosCardsHumanPlayer(card));
                    printCardsHumanPlayer();
                } else {
                    // Mostrar un mensaje o alerta indicando que la carta no es válida
                    System.out.println("Carta no válida. Debes jugar una carta del mismo color o número.");
                }
            });

            this.gridPaneCardsPlayer.add(cardImageView, i, 0);
        }
    }

    public void printCardsMachinePlayer() {
        gridPaneCardsMachine.getChildren().clear();
        Image backCardImage = new Image(getClass().getResource("/org/example/eiscuno/cards-uno/card_uno.png").toString());

        for (int i = 0; i < machinePlayer.getCardsPlayer().size() && i < 4; i++) {
            ImageView cardImageView = new ImageView(backCardImage);
            cardImageView.setFitHeight(90);
            cardImageView.setFitWidth(70);
            gridPaneCardsMachine.add(cardImageView, i, 0);

            cardImageView.setUserData(machinePlayer.getCardsPlayer().get(i));
        }
    }

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
        gameUno.eatCard(humanPlayer,1);
        printCardsHumanPlayer();
    }

    @FXML
    void onHandleUno(ActionEvent event) {
        // Implement logic to handle Uno event here
    }

    @Override
    public void updateMachineView() {
        Platform.runLater(this::printCardsMachinePlayer);
        Platform.runLater(this::printCardsHumanPlayer);
    }
    private void updateTableImageView() {
        Card topCard = table.getCurrentCardOnTheTable();
        if (topCard != null) {
            tableImageView.setImage(topCard.getImage());
        }
    }
    private void disablePlayerCards() {
        for (Card card : humanPlayer.getCardsPlayer()) {
            card.getCard().setDisable(true);
        }
    }

    private void enablePlayerCards() {
        for (Card card : humanPlayer.getCardsPlayer()) {
            card.getCard().setDisable(false);
        }
    }
}
