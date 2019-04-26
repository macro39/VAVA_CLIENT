package src.controller.employee;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import src.controller.Notification;
import src.model.Employee;

import java.io.IOException;
import java.util.ResourceBundle;

public class EmployeeBackToMenu extends Notification {

    public void backToMenu(AnchorPane rootPane, Employee employee, ResourceBundle actualLanguage) {
        Parent parent = null;
        try {
            FXMLLoader loaader = new FXMLLoader(
                    getClass().getResource("/view/employee/employee_menu.fxml"), actualLanguage);
            parent = (Parent) loaader.load();

            EmployeeMenuController employeeMenuController = loaader.getController();
            employeeMenuController.setEmployee(employee);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene newScene = new Scene(parent);

        //This line gets the Stage information
        Stage currentStage = (Stage) rootPane.getScene().getWindow();

        currentStage.setScene(newScene);
        currentStage.show();
    }

}
