package org.example.eiscuno.model.game;

public interface IGameEndSubject {
    void addGameEndObserver(IGameEndObserver observer);
    void removeGameEndObserver(IGameEndObserver observer);
    void notifyGameEndObservers(String winner);
}
