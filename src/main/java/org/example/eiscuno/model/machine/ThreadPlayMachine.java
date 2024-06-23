package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.game.IGameEndObserver;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * ThreadPlayMachine is a thread that handles the machine player's turn in the Uno game.
 * It plays cards automatically based on the game rules and notifies observers about changes in the game state.
 */
public class ThreadPlayMachine extends Thread implements IMachineSubject, IGameEndObserver {
    private Table table;
    private GameUno gameUno;
    private Player machinePlayer;
    private ImageView tableImageView;
    private volatile boolean hasPlayerPlayed;
    private List<IMachineObserver> observers = new ArrayList<>();
    private Runnable disablePlayerCards;
    private Runnable enablePlayerCards;
    private boolean running = true;
    private boolean isSpecial=false;

    /**
     * Constructs a new ThreadPlayMachine instance.
     *
     * @param table              the game table
     * @param machinePlayer      the machine player
     * @param tableImageView     the ImageView for the table
     * @param gameUno            the Uno game instance
     * @param disablePlayerCards the Runnable to disable player cards
     * @param enablePlayerCards  the Runnable to enable player cards
     */
    public ThreadPlayMachine(Table table, Player machinePlayer, ImageView tableImageView, GameUno gameUno, Runnable disablePlayerCards, Runnable enablePlayerCards) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.hasPlayerPlayed = false;
        this.gameUno = gameUno;
        this.disablePlayerCards = disablePlayerCards;
        this.enablePlayerCards = enablePlayerCards;
        gameUno.addGameEndObserver(this); // Add game end observer
    }

    /**
     * The run method of the thread. It handles the machine player's turn logic.
     */
    public void run() {
        while (running) {
            if (hasPlayerPlayed) {
                Platform.runLater(disablePlayerCards); // Disable player cards
                playTurn();
                hasPlayerPlayed = false;
                Platform.runLater(enablePlayerCards); // Enable player cards
            }
        }
    }

    /**
     * Plays the machine player's turn.
     */
    private void playTurn() {
        boolean specialCardPlayed;
        do {
            try {
                Thread.sleep(2000); // Wait for 2 seconds before each move
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            specialCardPlayed = putCardOnTheTable();
            notifyObservers();

            if (specialCardPlayed) {
                try {
                    Thread.sleep(2000); // Wait for 2 seconds if a special card was played
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } while (specialCardPlayed && running);
    }

    /**
     * Places a card on the table and updates the game state.
     *
     * @return true if a special card was played, false otherwise
     */
    private boolean putCardOnTheTable() {
        Card topCard = table.getCurrentCardOnTheTable();
        Card cardToPlay = null;

        // First, look for a "wild" card
        for (Card card : machinePlayer.getCardsPlayer()) {
            if (card.getColor().equals("NON_COLOR")) {
                cardToPlay = card;
                break;
            }
        }

        // If no "wild" card is found, look for a playable card
        if (cardToPlay == null) {
            for (Card card : machinePlayer.getCardsPlayer()) {
                if (gameUno.isCardPlayable(card, topCard)) {
                    cardToPlay = card;
                    break;
                }
            }
        }

        // Play the found card
        if (cardToPlay != null) {
            int pos = findPosCardsMachinePlayer(cardToPlay);
            if (pos != -1) {
                machinePlayer.removeCard(pos);
                table.addCardOnTheTable(cardToPlay);
                Card finalCardToPlay = cardToPlay;
                Platform.runLater(() -> {
                    tableImageView.setImage(finalCardToPlay.getImage());
                    notifyObservers();
                    System.out.println("La máquina tiró una carta. Le quedan " + machinePlayer.getCardsPlayer().size() + " cartas.");
                    this.isSpecial=gameUno.handleSpecialCards(finalCardToPlay, machinePlayer);
                    if (isSpecial) {
                        notifyObservers();
                    }
                });

                return isSpecial;
            }
        } else {
            // If no valid card is found, draw a card from the deck
            Platform.runLater(() -> {
                gameUno.eatCard(machinePlayer, 1);
                System.out.println("La máquina se comió una carta. Le quedan " + machinePlayer.getCardsPlayer().size() + " cartas.");
                notifyObservers();
            });
        }
        return false;
    }

    /**
     * Finds the position of a card in the machine player's hand.
     *
     * @param card the card to find
     * @return the position of the card, or -1 if not found
     */
    private Integer findPosCardsMachinePlayer(Card card) {
        for (int i = 0; i < machinePlayer.getCardsPlayer().size(); i++) {
            if (machinePlayer.getCardsPlayer().get(i).equals(card)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Sets the flag indicating that the player has played.
     *
     * @param hasPlayerPlayed the flag value
     */
    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }

    @Override
    public void attach(IMachineObserver observer) {
        observers.add(observer);
    }

    @Override
    public void detach(IMachineObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (IMachineObserver observer : observers) {
            observer.updateMachineView();
        }
    }

    @Override
    public void onGameEnd(String winner) {
        running = false;
    }

    /**
     * Stops the thread from running.
     */
    public void stopRunning() {
        running = false;
    }

    /**
     * Checks if the thread is still running.
     *
     * @return true if the thread is running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }
}
