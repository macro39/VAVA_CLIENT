package src.controller.admin;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import src.controller.Notification;
import src.model.Employee;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ResourceBundle;

public class AdminAddEmployeeController extends Notification implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label labelFirstName;
    @FXML
    private Label labelLastName;
    @FXML
    private Label labelDate;
    @FXML
    private JFXTextField textFieldFirstName;
    @FXML
    private JFXTextField textFieldLastName;
    @FXML
    private JFXTextField textFieldLogin;
    @FXML
    private JFXTextField textFieldPassword;
    @FXML
    private JFXTextField textPhoneNumber;
    @FXML
    private JFXComboBox comboBoxType;

    private Employee admin;

    private ResourceBundle actualLanguage;

    public void setHeader () {
        labelFirstName.setText(admin.getFirstName());
        labelLastName.setText(admin.getLastName());

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime());

        labelDate.setText(time);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        actualLanguage = resources;

        addItemsComboBox();
    }

    public void addItemsComboBox() {
        String resourceURL = "http://localhost:8080/api/employee/getTypes";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        //headers.set("Content-Type","application/json");
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<ArrayList<String>> entity = new HttpEntity<>(headers);

        ResponseEntity<ArrayList> result = restTemplate.exchange(resourceURL, HttpMethod.GET, entity, ArrayList.class);

        comboBoxType.getItems().addAll(result.getBody());
    }

    public void setAdmin(Employee admin) {
        this.admin = admin;
        setHeader();
    }

    public String getFirstName() {
        return textFieldFirstName.getText();
    }

    public String getLastName() {
        return textFieldLastName.getText();
    }

    public String getLogin() {
        return textFieldLogin.getText();
    }

    public String getPassword() {
        return textFieldPassword.getText();
    }

    public String getPhone() { return textPhoneNumber.getText(); }

    public String getType() {
        if(comboBoxType.getSelectionModel().isEmpty()) {
            return null;
        }
        return comboBoxType.getSelectionModel().getSelectedItem().toString();
    }

    public boolean tooLongText(){
        if (getFirstName().length() > 255 ||
                getLastName().length() > 255 ||
                getLogin().length() > 255 ||
                getPassword().length() > 255 ||
                getPhone().length() > 255) {
            return true;
        }
        return false;
    }

    public boolean emptyFieldChecker() {
        if (getFirstName().trim().isEmpty() ||
                getLastName().trim().isEmpty() ||
                getLogin().trim().isEmpty() ||
                getPassword().trim().isEmpty() ||
                getPhone().trim().isEmpty() ||
                getType() == null) {
            return true;
        }
        return false;
    }

    public void btnAddEmployeePushed(ActionEvent actionEvent) {
        if(tooLongText()) {
            showWarning("Príliš dlhé údaje. Max. veľkosť jedného údaju je 254 znakov!");
            return;
        }
        if(emptyFieldChecker()) {
            showError("Vypíšte správne všetky údaje!");
            return;
        }

        Employee employee = new Employee(getFirstName(),getLastName(),getLogin(),getPassword(),getPhone(),getType());

        String resourceURL = "http://localhost:8080/api/employee/";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<Employee> entity = new HttpEntity<>(employee, headers);

        ResponseEntity<Boolean> result = restTemplate.exchange(resourceURL, HttpMethod.POST, entity, Boolean.class);

        if(result.getBody()) {
            showConfirm("Konto zamestnanca bolo úspešne vytvorené!");
            backToMenu();
        } else {
            showError("Zadaný login - " + getLogin() + " - už existuje!");
        }
    }

    public void btnBackPushed(ActionEvent actionEvent) {
      backToMenu();
    }

    public void backToMenu() {
        Parent parent = null;
        try {
            FXMLLoader loaader = new FXMLLoader(
                    getClass().getResource("/view/admin/admin_menu.fxml"), actualLanguage);
            parent = (Parent) loaader.load();

            AdminMenuController adminMenuController = loaader.getController();
            adminMenuController.setAdmin(admin);

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
