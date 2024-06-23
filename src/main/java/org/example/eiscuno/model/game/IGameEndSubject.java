package org.example.eiscuno.model.game;

/**
 * Subject interface for notifying observers about the end of the game.
 * Classes that implement this interface can add, remove, and notify observers about the game's end.
 */
public interface IGameEndSubject {

    /**
     * Adds an observer to be notified when the game ends.
     *
     * @param observer The observer to be added.
     */
    void addGameEndObserver(IGameEndObserver observer);

    /**
     * Removes an observer from the notification list.
     *
     * @param observer The observer to be removed.
     */
    void removeGameEndObserver(IGameEndObserver observer);

    /**
     * Notifies all observers that the game has ended.
     *
     * @param winner The winner of the game.
     */
    void notifyGameEndObservers(String winner);
}
