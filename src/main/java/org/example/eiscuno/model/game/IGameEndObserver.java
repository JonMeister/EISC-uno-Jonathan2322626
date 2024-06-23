package org.example.eiscuno.model.game;

/**
 * Observer interface for the end of the game.
 * Classes that implement this interface will be notified when the game ends.
 */
public interface IGameEndObserver {

    /**
     * Called when the game ends.
     *
     * @param winner The winner of the game.
     */
    void onGameEnd(String winner);
}
