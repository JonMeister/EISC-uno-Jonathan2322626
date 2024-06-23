package org.example.eiscuno.view.alert;

/**
 * The interface defines methods that can be implemented by classes that want to provide alert functionality.
 * @author Jonathan Aristizabal
 * @version 1.0
 */
public interface IAlertBox {
    void showError(String title, String header, String content);
    void showInfo(String title, String header, String content);
}
