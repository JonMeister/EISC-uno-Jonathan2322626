package org.example.eiscuno.model.machine;

/**
 * Interface representing an observer that updates the machine's view in the game.
 */
public interface IMachineObserver {

    /**
     * Updates the machine's view in response to changes in the game state.
     */
    void updateMachineView();
}
