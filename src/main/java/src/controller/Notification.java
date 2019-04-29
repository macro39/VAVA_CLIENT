package src.controller;

import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 * @author Kamil
 */
public class Notification {

    public void showInformation(String message) {
        Notifications notification = Notifications.create()
                .title(message)
                .hideAfter(Duration.seconds(4))
                .hideCloseButton();
        notification.showInformation();
    }

    public void showConfirm(String message) {
        Notifications notification = Notifications.create()
                .title(message)
                .hideAfter(Duration.seconds(4))
                .hideCloseButton();
        notification.showConfirm();
    }

    public void showWarning(String message) {
        Notifications notification = Notifications.create()
                .title(message)
                .hideAfter(Duration.seconds(4))
                .hideCloseButton();
        notification.showWarning();
    }

    public void showError(String message) {
        Notifications notification = Notifications.create()
                .title(message)
                .hideAfter(Duration.seconds(4))
                .hideCloseButton();
        notification.showError();
    }
}
