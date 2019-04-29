package src.controller.employee;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import src.model.Employee;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;

public class EmployeeSearchMenuController extends EmployeeBackToMenu implements Initializable {

    @FXML
    private AnchorPane rootPane;
    @FXML private Label labelFirstName;
    @FXML private Label labelDate;
    @FXML private Label labelLastName;

    private Employee employee;

    private ResourceBundle actualLanguage;

    public void setHeader () {
        labelFirstName.setText(employee.getFirstName());
        labelLastName.setText(employee.getLastName());

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime());

        labelDate.setText(time);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        actualLanguage = resources;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        setHeader();
    }

    public void btnSearchCustomerPushed(ActionEvent actionEvent) {
        Parent parent = null;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/employee/employee_search_customer.fxml"), actualLanguage);
            parent = (Parent) loader.load();

            EmployeeSearchCustomerController employeeSearchCustomerController = loader.getController();
            employeeSearchCustomerController.setEmployee(employee);
            employeeSearchCustomerController.addItemsToList();
            employeeSearchCustomerController.addItemsToTable();
            employeeSearchCustomerController.setNewRangeOfDisplayedData();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene newScene = new Scene(parent);

        //This line gets the Stage information
        Stage currentStage = (Stage) rootPane.getScene().getWindow();

        currentStage.setScene(newScene);
        currentStage.show();
    }

    public void btnSearchCarPushed(ActionEvent actionEvent) {
        Parent parent = null;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/employee/employee_search_car.fxml"), actualLanguage);
            parent = (Parent) loader.load();

            EmployeeSearchCarController employeeSearchCarController = loader.getController();
            employeeSearchCarController.setEmployee(employee);
            employeeSearchCarController.addItemsToList();
            employeeSearchCarController.addItemsToTable();
            employeeSearchCarController.setNewRangeOfDisplayedData();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene newScene = new Scene(parent);

        //This line gets the Stage information
        Stage currentStage = (Stage) rootPane.getScene().getWindow();

        currentStage.setScene(newScene);
        currentStage.show();
    }

    public void btnSearchContractPushed(ActionEvent actionEvent) {
        Parent parent = null;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/employee/employee_search_contract.fxml"), actualLanguage);
            parent = (Parent) loader.load();

            EmployeeSearchContractController employeeSearchContractController = loader.getController();
            employeeSearchContractController.setEmployee(employee);
            employeeSearchContractController.addItemsToList();
            employeeSearchContractController.addItemsToTable();
            employeeSearchContractController.setNewRangeOfDisplayedData();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene newScene = new Scene(parent);

        //This line gets the Stage information
        Stage currentStage = (Stage) rootPane.getScene().getWindow();

        currentStage.setScene(newScene);
        currentStage.show();
    }

    public void btnBackPushed(ActionEvent actionEvent) {
        backToMenu(rootPane,employee,actualLanguage);
    }
}
