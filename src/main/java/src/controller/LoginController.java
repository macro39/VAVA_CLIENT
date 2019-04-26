package src.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import src.controller.admin.AdminMenuController;
import src.model.Employee;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController extends Notification implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button buttonLogIn;

    @FXML
    private JFXTextField fieldLogin;
    @FXML
    private JFXPasswordField fieldPassword;

    private ResourceBundle actualLanguage;

    public void initialize(URL location, ResourceBundle resources) {
        buttonLogIn.setDefaultButton(true);

        actualLanguage = resources;

        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(2000));
        fadeTransition.setNode(rootPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);

        fadeTransition.play();
    }

    public String getLogin() {
        return fieldLogin.getText();
    }

    public String getPassword() { return fieldPassword.getText(); }

    public boolean emptyFieldChecker() {
        if(getLogin().trim().isEmpty() || getPassword().trim().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public void logIn(ActionEvent actionEvent) {

        if(emptyFieldChecker()) {
            showError("Zadaj údaje!");
            return;
        }

        // need to catch if server is off

        String resourceURL = "http://localhost:8080/api/employee/" + getLogin() + "/" + getPassword();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        //headers.set("Content-Type","application/json");
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<Employee> entity = new HttpEntity<>(headers);

        ResponseEntity<Employee> result = restTemplate.exchange(resourceURL, HttpMethod.GET, entity, Employee.class);

        if(result.getBody() == null) {
            showError("Nesprávne prihlasovacie údaje!");
        } else {

            if(result.getBody().getType().equals("admin")) {
                setNewScene("/view/admin/admin_menu.fxml", result.getBody());
            } else {

            }
            showConfirm("Prihlásenie prebehlo úspešne!");
        }
    }

    public void setNewScene(String nextScene, Employee employee) {

        Parent parent = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(nextScene), actualLanguage);
            parent = (Parent) loader.load();


            if(employee.getType().equals("admin")) {
                AdminMenuController adminMenuController = loader.getController();
                adminMenuController.setAdmin(employee);
            } else {
//                EmployeeMenuController employeeMenuController = loaader.getController();
//                employeeMenuController.setEmployee(employee);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene newScene = new Scene(parent);

        //This line gets the Stage information
        Stage currentStage = (Stage) rootPane.getScene().getWindow();

        currentStage.setScene(newScene);
        currentStage.show();
    }

    public void buttonSKlangPushed(ActionEvent actionEvent) {

        if(actualLanguage.getLocale().toString().equals("en_EN")) {
            actualLanguage = ResourceBundle.getBundle("lang",new Locale("sk","SK"));

            changeLanguageInGUI();
        }
    }

    public void buttonENlangPushed(ActionEvent actionEvent) {

        if(actualLanguage.getLocale().toString().equals("sk_SK")) {
            actualLanguage = ResourceBundle.getBundle("lang",new Locale("en","EN"));

            changeLanguageInGUI();
        }
    }

    public void changeLanguageInGUI() {
        buttonLogIn.setText(actualLanguage.getString("loginButton"));
        fieldLogin.setPromptText(actualLanguage.getString("usernameText"));
        fieldPassword.setPromptText(actualLanguage.getString("passwordText"));
    }
}
