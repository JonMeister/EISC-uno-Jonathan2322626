package org.example.eiscuno.model.game;

import javafx.application.Platform;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a game of Uno.
 * This class manages the game logic and interactions between players, deck, and the table.
 */
public class GameUno implements IGameUno, IGameEndSubject {

    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;
    private List<IGameEndObserver> gameEndObservers = new ArrayList<>();

    /**
     * Constructs a new GameUno instance.
     *
     * @param humanPlayer   The human player participating in the game.
     * @param machinePlayer The machine player participating in the game.
     * @param deck          The deck of cards used in the game.
     * @param table         The table where cards are placed during the game.
     */
    public GameUno(Player humanPlayer, Player machinePlayer, Deck deck, Table table) {
        this.humanPlayer = humanPlayer;
        this.machinePlayer = machinePlayer;
        this.deck = deck;
        this.table = table;
        this.gameEndObservers = new ArrayList<>();
        deck.setPlayers(humanPlayer, machinePlayer); // Set players in the deck
    }

    /**
     * Starts the Uno game by distributing cards to players.
     * The human player and the machine player each receive 10 cards from the deck.
     */
    @Override
    public void startGame() {
        for (int i = 0; i < 10; i++) {
            if (i < 5) {
                humanPlayer.addCard(this.deck.takeCard());
            } else {
                machinePlayer.addCard(this.deck.takeCard());
            }
        }

        // Play a numeric card to start the game
        Card initialCard;
        do {
            initialCard = deck.takeCard();
        } while (!isNumericCard(initialCard));
        table.addCardOnTheTable(initialCard);
        deck.addPlayedCard(initialCard); // Add the initial card to the played cards
    }

    /**
     * Checks if a card is numeric.
     *
     * @param card The card to check.
     * @return True if the card is numeric, false otherwise.
     */
    private boolean isNumericCard(Card card) {
        try {
            Integer.parseInt(card.getValue());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Allows a player to draw a specified number of cards from the deck.
     *
     * @param player        The player who will draw cards.
     * @param numberOfCards The number of cards to draw.
     */
    @Override
    public void eatCard(Player player, int numberOfCards) {
        checkGameEnd();
        for (int i = 0; i < numberOfCards; i++) {
            player.addCard(this.deck.takeCard());
        }
    }

    /**
     * Places a card on the table during the game.
     *
     * @param card The card to be placed on the table.
     */
    @Override
    public void playCard(Card card) {
        this.table.addCardOnTheTable(card);
        this.deck.addPlayedCard(card); // Add the card to the played cards
        Platform.runLater(this::checkGameEnd);
    }

    /**
     * Handles special cards like +2, +4, SKIP, and REVERSE.
     *
     * @param card   The card to handle.
     * @param player The player who played the card.
     * @return True if the card is a special card that requires a turn change, false otherwise.
     */
    public boolean handleSpecialCards(Card card, Player player) {
        switch (card.getValue()) {
            case "+2" -> {
                if (player.equals(humanPlayer)) {
                    eatCard(machinePlayer, 2);
                    System.out.println("Human Player hace comer +2 a la máquina.");
                } else {
                    eatCard(humanPlayer, 2);
                    System.out.println("Machine Player hace comer +2 a la persona.");

                }
                checkGameEnd();
                return true;
            }
            case "+4" -> {
                if (player.equals(humanPlayer)) {
                    eatCard(machinePlayer, 4);
                    System.out.println("Human Player hace comer +4 a la máquina.");

                } else {
                    eatCard(humanPlayer, 4);
                    System.out.println("Machine Player hace comer +4 a la persona.");

                }
                checkGameEnd();
                return true;
            }
            case "SKIP", "RESERVE" -> {
                checkGameEnd();
                return true;
            }
            default -> {
                checkGameEnd();
                return false;
            }
        }
    }

    /**
     * Checks if a card can be played on top of another card.
     *
     * @param card    The card to be played.
     * @param topCard The card on top of the table.
     * @return True if the card can be played, false otherwise.
     */
    public boolean isCardPlayable(Card card, Card topCard) {
        // Allow playing any card if the top card is NON_COLOR
        if (topCard.getColor().equals("NON_COLOR") || card.getColor().equals("NON_COLOR")) {
            return true;
        }
        return card.getColor().equals(topCard.getColor()) || card.getValue().equals(topCard.getValue());
    }

    /**
     * Handles the scenario when a player shouts "Uno", forcing the other player to draw a card.
     *
     * @param playerWhoSang The player who shouted "Uno".
     */
    @Override
    public void haveSungOne(String playerWhoSang) {
        if (playerWhoSang.equals("HUMAN_PLAYER")) {
            machinePlayer.addCard(this.deck.takeCard());
        } else {
            humanPlayer.addCard(this.deck.takeCard());
        }
    }

    /**
     * Retrieves the current visible cards of the human player starting from a specific position.
     *
     * @param posInitCardToShow The initial position of the cards to show.
     * @return An array of cards visible to the human player.
     */
    @Override
    public Card[] getCurrentVisibleCardsHumanPlayer(int posInitCardToShow) {
        int totalCards = this.humanPlayer.getCardsPlayer().size();
        int numVisibleCards = Math.min(4, totalCards - posInitCardToShow);
        Card[] cards = new Card[numVisibleCards];

        for (int i = 0; i < numVisibleCards; i++) {
            cards[i] = this.humanPlayer.getCard(posInitCardToShow + i);
        }

        return cards;
    }

    /**
     * Checks if the game is over.
     *
     * @return True if the deck is empty, indicating the game is over; otherwise, false.
     */
    @Override
    public Boolean isGameOver() {
        return humanPlayer.getCardsPlayer().isEmpty() || machinePlayer.getCardsPlayer().isEmpty();
    }

    /**
     * Adds an observer for the game end.
     *
     * @param observer The observer to add.
     */
    @Override
    public void addGameEndObserver(IGameEndObserver observer) {
        gameEndObservers.add(observer);
    }

    /**
     * Removes an observer for the game end.
     *
     * @param observer The observer to remove.
     */
    @Override
    public void removeGameEndObserver(IGameEndObserver observer) {
        gameEndObservers.remove(observer);
    }

    /**
     * Notifies all observers that the game has ended.
     *
     * @param winner The winner of the game.
     */
    @Override
    public void notifyGameEndObservers(String winner) {
        for (IGameEndObserver observer : gameEndObservers) {
            observer.onGameEnd(winner);
        }
    }

    /**
     * Checks if the game has ended and notifies observers if it has.
     */
    private void checkGameEnd() {
        if (isGameOver()) {
            String winner = humanPlayer.getCardsPlayer().isEmpty() ? "Human Player" : "Machine Player";
            notifyGameEndObservers(winner);
        }
    }
}
