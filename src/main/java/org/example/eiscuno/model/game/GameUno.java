package org.example.eiscuno.model.game;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

/**
 * Represents a game of Uno.
 * This class manages the game logic and interactions between players, deck, and the table.
 */
public class GameUno implements IGameUno {

    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;

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

        // Lanzar una carta numérica al iniciar el juego
        Card initialCard;
        do {
            initialCard = deck.takeCard();
        } while (!isNumericCard(initialCard));
        table.addCardOnTheTable(initialCard);
    }
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
    }
    public boolean handleSpecialCards(Card card, Player player) {
        // Verifica si la carta jugada es una carta +2
        switch (card.getValue()) {
            case "+2" -> {
                // Si la carta jugada pertenece al jugador humano
                if (player.equals(humanPlayer)) {
                    // El jugador máquina toma 2 cartas
                    eatCard(machinePlayer, 2);
                } else {
                    // El jugador humano toma 2 cartas
                    eatCard(humanPlayer, 2);
                }
                return true;
            }

            // Verifica si la carta jugada es una carta +4
            case "+4" -> {
                // Si la carta jugada pertenece al jugador humano
                if (player.equals(humanPlayer)) {
                    // El jugador máquina toma 4 cartas
                    eatCard(machinePlayer, 4);
                } else {
                    // El jugador humano toma 4 cartas
                    eatCard(humanPlayer, 4);
                }
                return true;
            }
            case "SKIP"-> {
                return true;}
            case "RESERVE"-> {
                return true;}

            default -> {
                return false;
            }
        }
    }

    public boolean isCardPlayable(Card card, Card topCard) {
        // Permitir jugar cualquier carta si la carta en la cima es NON_COLOR
        return true;
      /*  if (topCard.getColor().equals("NON_COLOR") || card.getColor().equals("NON_COLOR")) {
            return true;
        }
        return card.getColor().equals(topCard.getColor()) || card.getValue().equals(topCard.getValue());*/
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
        return null;
    }
}
