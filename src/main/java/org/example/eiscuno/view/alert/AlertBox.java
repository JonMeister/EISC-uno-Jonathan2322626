package org.example.eiscuno.view.alert;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * This class provides methods to display different types of alert messages in the sudoku app.
 * Implements IAlertbox
 * @author Jonathan Aristizabal
 * @version 1.0
 */
public class AlertBox implements IAlertBox {
    /**
     * Displays an error alert message with a title, header, and content.
     * @param title The title of the alert window.
     * @param header The header text displayed within the alert window.
     * @param content The main content text displayed within the alert window.
     */
    @Override
    public void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/org/example/eiscuno/favicon.png")))); // To add an icon
        alert.showAndWait();
    }
    /**
     * Displays an information alert message with a title, header, and content.
     * @param title The title of the alert window.
     * @param header The header text displayed within the alert window.
     * @param content The main content text displayed within the alert window.
     */
    @Override
    public void showInfo(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/org/example/eiscuno/favicon.png")))); // To add an icon
        alert.showAndWait();

    }
}
