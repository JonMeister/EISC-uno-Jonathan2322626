package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.game.IGameEndObserver;

import java.util.ArrayList;

/**
 * ThreadSingUNOMachine is a thread that monitors the player's cards and calls "UNO" when the player has one card left.
 */
public class ThreadSingUNOMachine implements Runnable, IGameEndObserver {
    private ArrayList<Card> cardsPlayer;
    private Runnable machineCallsUNO;
    private boolean running = true;

    /**
     * Constructs a new ThreadSingUNOMachine instance.
     *
     * @param cardsPlayer     the list of player's cards
     * @param machineCallsUNO the Runnable that handles the machine calling "UNO"
     */
    public ThreadSingUNOMachine(ArrayList<Card> cardsPlayer, Runnable machineCallsUNO) {
        this.cardsPlayer = cardsPlayer;
        this.machineCallsUNO = machineCallsUNO;
    }

    /**
     * The run method of the thread. It continuously checks if the player has one card left.
     */
    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(2000 + (long) (Math.random() * 3000)); // Wait for 2 to 5 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hasOneCardTheHumanPlayer();
        }
    }

    /**
     * Checks if the player has one card left and triggers the machine to call "UNO".
     */
    private void hasOneCardTheHumanPlayer() {
        if (cardsPlayer.size() == 1) {
            Platform.runLater(machineCallsUNO);
        }
    }

    /**
     * Handles the game end event.
     *
     * @param winner the winner of the game
     */
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
