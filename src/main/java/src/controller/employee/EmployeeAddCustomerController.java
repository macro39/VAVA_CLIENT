package src.controller.employee;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import src.controller.LoginController;
import src.model.Customer;
import src.model.Employee;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmployeeAddCustomerController extends EmployeeBackToMenu implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private JFXTextField textFieldFirstName;
    @FXML
    private JFXTextField textFieldLastName;
    @FXML
    private JFXTextField textFieldID;
    @FXML
    private JFXTextField textFieldAddress;
    @FXML
    private JFXTextField textFieldBankAccount;
    @FXML
    private JFXTextField textFieldPhone;

    private Employee employee;

    private ResourceBundle actualLanguage;

    private static Logger LOG = Logger.getLogger(LoginController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        actualLanguage = resources;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getFirstName(){
        return textFieldFirstName.getText();
    }

    public String getLastName() {
        return textFieldLastName.getText();
    }

    public String getID(){
        return textFieldID.getText();
    }

    public String getAddress(){
        return textFieldAddress.getText();
    }

    public String getBankAccount(){
        return textFieldBankAccount.getText();
    }

    public String getPhone(){
        return textFieldPhone.getText();
    }

    public boolean tooLongTextChecker(){
        if(getLastName().length() > 255 ||
                getID().length() > 8 ||
                getFirstName().length()>255 ||
                getAddress().length()>255 ||
                getBankAccount().length()>30 ||
                getPhone().length()>255){
            return true;
        }
        return false;
    }

    public boolean emptyFieldChecker() {
        if (getFirstName().trim().isEmpty() ||
                getAddress().trim().isEmpty() ||
                getBankAccount().trim().isEmpty() ||
                getLastName().trim().isEmpty() ||
                getPhone().trim().isEmpty() ||
                getID().trim().isEmpty()) {
            return true;
        }
        return false;
    }

    public void btnAddPushed(ActionEvent actionEvent) {

        if(tooLongTextChecker()) {
            showError(actualLanguage.getString("notificationBadEnterDate"));
        } else if(emptyFieldChecker()) {
            showWarning(actualLanguage.getString("notificationNoEnterData"));
        } else {
            Customer newCustomer = new Customer(getID().toUpperCase(),
                    getFirstName(),getLastName(),getAddress(),getBankAccount(),getPhone());

            String resourceURL = "http://localhost:8080/api/customer";

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<Customer> entity = new HttpEntity<>(newCustomer, headers);

            ResponseEntity<Boolean> result;

            try {
                result = restTemplate.exchange(resourceURL, HttpMethod.POST, entity, Boolean.class);
            } catch (Exception e) {
                LOG.log(Level.WARNING, actualLanguage.getString("notificationNoResponseServer"));
                showError(actualLanguage.getString("notificationNoResponseServer"));
                return;
            }

            if(!result.getBody()) {
                showError(actualLanguage.getString("notificationBadId"));
            } else {
                showConfirm(actualLanguage.getString("notificationAddBorrower"));
                backToMenu(rootPane,employee,actualLanguage);
            }
        }
    }

    public void btnBackPushed(ActionEvent actionEvent) {
        backToMenu(rootPane,employee,actualLanguage);
    }
}
