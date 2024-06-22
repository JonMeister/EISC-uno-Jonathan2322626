package org.example.eiscuno.model.machine;

public interface IMachineSubject {
    void attach(IMachineObserver observer);
    void detach(IMachineObserver observer);
    void notifyObservers();
}
