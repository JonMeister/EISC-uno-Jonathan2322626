package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.game.IGameEndObserver;

import java.util.ArrayList;

public class ThreadSingUNOMachine implements Runnable, IGameEndObserver {
    private ArrayList<Card> cardsPlayer;
    private Runnable machineCallsUNO;
    private boolean running = true;

    public ThreadSingUNOMachine(ArrayList<Card> cardsPlayer, Runnable machineCallsUNO) {
        this.cardsPlayer = cardsPlayer;
        this.machineCallsUNO = machineCallsUNO;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(2000 + (long) (Math.random() * 3000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hasOneCardTheHumanPlayer();
        }
    }

    private void hasOneCardTheHumanPlayer() {
        if (cardsPlayer.size() == 1) {
            Platform.runLater(machineCallsUNO);
        }
    }
    @Override
    public void onGameEnd(String winner) {
        running = false;
    }
    public void stopRunning() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
