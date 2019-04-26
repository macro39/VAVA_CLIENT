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

public class EmployeeMenuController implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label labelFirstName;
    @FXML
    private Label labelLastName;
    @FXML
    private Label labelDate;

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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        setHeader();
    }


    public void btnAddCustomerPushed(ActionEvent actionEvent) {
        Parent parent = null;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/employee/employee_add_customer.fxml"), actualLanguage);
            parent = (Parent) loader.load();

            EmployeeAddCustomerController employeeAddCustomerController= loader.getController();
            employeeAddCustomerController.setEmployee(employee);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene newScene = new Scene(parent);

        //This line gets the Stage information
        Stage currentStage = (Stage) rootPane.getScene().getWindow();

        currentStage.setScene(newScene);
        currentStage.show();
    }

    public void btnCreateCarPushed(ActionEvent actionEvent) {
    }

    public void btnCreateAgreement(ActionEvent actionEvent) {
    }

    public void btnSearchingPushed(ActionEvent actionEvent) {
        Parent parent = null;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/employee/employee_search_menu.fxml"), actualLanguage);
            parent = (Parent) loader.load();

            EmployeeSearchMenuController employeeSearchMenuController = loader.getController();
            employeeSearchMenuController.setEmployee(employee);


        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene newScene = new Scene(parent);

        //This line gets the Stage information
        Stage currentStage = (Stage) rootPane.getScene().getWindow();

        currentStage.setScene(newScene);
        currentStage.show();
    }

    public void btnLogoutPushed(ActionEvent actionEvent) {
        AnchorPane pane = null;
        try {
            pane = FXMLLoader.load(getClass().getResource("/view/login.fxml"), actualLanguage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        rootPane.getChildren().setAll(pane);
    }
}
