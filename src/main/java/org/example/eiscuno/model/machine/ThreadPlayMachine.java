package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

import java.util.ArrayList;
import java.util.List;

public class ThreadPlayMachine extends Thread implements IMachineSubject {
    private Table table;
    private GameUno gameUno;
    private Player machinePlayer;
    private ImageView tableImageView;
    private volatile boolean hasPlayerPlayed;
    private List<IMachineObserver> observers = new ArrayList<>();
    private Runnable disablePlayerCards;
    private Runnable enablePlayerCards;

    public ThreadPlayMachine(Table table, Player machinePlayer, ImageView tableImageView, GameUno gameUno, Runnable disablePlayerCards, Runnable enablePlayerCards) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.hasPlayerPlayed = false;
        this.gameUno = gameUno;
        this.disablePlayerCards = disablePlayerCards;
        this.enablePlayerCards = enablePlayerCards;
    }

    public void run() {
        while (true) {
            if (hasPlayerPlayed) {
                Platform.runLater(disablePlayerCards); // Deshabilitar las cartas del jugador
                playTurn();
                hasPlayerPlayed = false;
                Platform.runLater(enablePlayerCards); // Habilitar las cartas del jugador
            }
        }
    }

    private void playTurn() {
        boolean specialCardPlayed;
        do {
            try {
                Thread.sleep(2000); // Espera de 2 segundos antes de cada jugada
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            specialCardPlayed = putCardOnTheTable();
            notifyObservers();

            // Esperar 2 segundos si se jugó una carta especial
            if (specialCardPlayed) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } while (specialCardPlayed);
    }

    private boolean putCardOnTheTable() {
        Card topCard = table.getCurrentCardOnTheTable(); // Obtén la carta en la cima de la pila de descartes
        Card cardToPlay = null;

        // Buscar primero una carta "wild"
        for (Card card : machinePlayer.getCardsPlayer()) {
            if (card.getColor().equals("NON_COLOR")) { // Suponiendo que "NON_COLOR" representa una carta wild
                cardToPlay = card;
                break;
            }
        }

        // Si no se encontró una carta "wild", buscar una carta jugable
        if (cardToPlay == null) {
            for (Card card : machinePlayer.getCardsPlayer()) {
                if (gameUno.isCardPlayable(card, topCard)) {
                    cardToPlay = card;
                    break;
                }
            }
        }

        // Si se encuentra una carta jugable, jugarla
        if (cardToPlay != null) {
            int pos = findPosCardsMachinePlayer(cardToPlay); // Encuentra la posición de la carta
            if (pos != -1) {
                machinePlayer.removeCard(pos); // Remueve la carta de la mano del jugador
                table.addCardOnTheTable(cardToPlay); // Añade la carta a la mesa
                Card finalCardToPlay = cardToPlay;
                Platform.runLater(() -> {
                    tableImageView.setImage(finalCardToPlay.getImage()); // Actualiza la imagen de la mesa
                    notifyObservers(); // Notifica a los observadores para actualizar la vista
                    System.out.println("La máquina tiró una carta. Le quedan " + machinePlayer.getCardsPlayer().size() + " cartas.");
                });

                boolean specialCard = gameUno.handleSpecialCards(finalCardToPlay, machinePlayer);
                if (specialCard) {
                    try {
                        Thread.sleep(2000); // Espera de 2 segundos antes de la próxima jugada
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return specialCard; // Retorna verdadero si la carta especial requiere un turno adicional
            }
        } else {
            // Lógica para cuando no hay una carta válida (puede ser tomar una carta del mazo)
            Platform.runLater(() -> {
                gameUno.eatCard(machinePlayer, 1);
                System.out.println("La máquina se comió una carta. Le quedan " + machinePlayer.getCardsPlayer().size() + " cartas.");
                notifyObservers(); // Notifica a los observadores para actualizar la vista
            });

            // Verificar si después de comer una carta puede jugar alguna carta
            for (Card card : machinePlayer.getCardsPlayer()) {
                if (gameUno.isCardPlayable(card, topCard)) {
                    cardToPlay = card;
                    break;
                }
            }

            if (cardToPlay != null) {
                int pos = findPosCardsMachinePlayer(cardToPlay); // Encuentra la posición de la carta
                if (pos != -1) {
                    machinePlayer.removeCard(pos); // Remueve la carta de la mano del jugador
                    table.addCardOnTheTable(cardToPlay); // Añade la carta a la mesa
                    Card finalCardToPlay = cardToPlay;
                    Platform.runLater(() -> {
                        tableImageView.setImage(finalCardToPlay.getImage()); // Actualiza la imagen de la mesa
                        notifyObservers(); // Notifica a los observadores para actualizar la vista
                        System.out.println("La máquina tiró una carta. Le quedan " + machinePlayer.getCardsPlayer().size() + " cartas.");
                    });

                    boolean specialCard = gameUno.handleSpecialCards(finalCardToPlay, machinePlayer);
                    if (specialCard) {
                        try {
                            Thread.sleep(2000); // Espera de 2 segundos antes de la próxima jugada
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return specialCard; // Retorna verdadero si la carta especial requiere un turno adicional
                }
            }
        }
        return false; // Retorna falso si no se requiere un turno adicional
    }

    private Integer findPosCardsMachinePlayer(Card card) {
        for (int i = 0; i < machinePlayer.getCardsPlayer().size(); i++) {
            if (machinePlayer.getCardsPlayer().get(i).equals(card)) {
                return i;
            }
        }
        return -1;
    }

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
}
