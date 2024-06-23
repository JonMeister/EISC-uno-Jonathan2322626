package org.example.eiscuno.model.machine;

/**
 * Interface representing a subject in the observer pattern for the machine's view in the game.
 */
public interface IMachineSubject {

    /**
     * Attaches an observer to the subject.
     *
     * @param observer the observer to be attached
     */
    void attach(IMachineObserver observer);

    /**
     * Detaches an observer from the subject.
     *
     * @param observer the observer to be detached
     */
    void detach(IMachineObserver observer);

    /**
     * Notifies all attached observers about changes in the game state.
     */
    void notifyObservers();
}
