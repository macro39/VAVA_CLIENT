package src.controller.employee;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import src.model.Contract;
import src.model.Employee;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jozef
 */
public class EmployeeContractDetailController extends EmployeeBackToMenu implements Initializable {

    @FXML private AnchorPane rootPane;

    // labels for customer
    @FXML private Label labelFirstName;
    @FXML private Label labelLastName;
    @FXML private Label labelCustomerId;
    @FXML private Label labelAdress;
    @FXML private Label labelBankAccount;
    @FXML private Label labelCustomerPhone;

    // labels for car
    @FXML private Label labelVIN;
    @FXML private Label labelSPZ;
    @FXML private Label labelBrand;
    @FXML private Label labelModel;
    @FXML private Label labelYear;
    @FXML private Label labelMileage;
    @FXML private Label labelFuel;
    @FXML private Label labelEngineCapacity;
    @FXML private Label labelEnginePower;
    @FXML private Label labelGearBox;
    @FXML private Label labelBodyStyle;
    @FXML private Label labelColor;
    @FXML private Label labelPrice;

    @FXML private Button buttonCreateContract;
    @FXML private Button buttonBack;

    // labels for employee
    @FXML private Label labelEmployeeFirstName;
    @FXML private Label labelEmployeeLastName;

    // labels for contract info
    @FXML private Label labelDateFromTo;
    @FXML private Label labelDateOfContract;
    @FXML private Label labelTotalPrice;

    // for save the data from database, or previously got from create contract scene [optional]
    private Date dateFROM = null;
    private Date dateTO = null;
    private Contract contract;

    // logged employee
    private Employee employee;

    private ResourceBundle actualLanguage;

    private Boolean isOpenedAsDetail = false;

    private static Logger LOG = Logger.getLogger(EmployeeContractDetailController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        actualLanguage = resources;

    }

    public void setAllLabels() {
        setEmployeeLabels();
        setAnotherLabels();
    }

    public void setEmployeeLabels() {
        setLabelEmployeeFirstName(employee.getFirstName());
        setLabelEmployeeLastName(employee.getLastName());
    }

    public void setAnotherLabels() {
        setLabelDateOfContract(contract.getDateOfCreating().toString());

        setLabelDateFromTo(actualLanguage.getString("loanDateLabel") +
                contract.getDateFrom() + " " + actualLanguage.getString(("toBeReturnDate")) +
                contract.getDateTo());

        setLabelVIN(contract.getCar().getCarVIN());
        setLabelSPZ(contract.getCar().getCarSPZ());
        setLabelBrand(contract.getCar().getBrand());
        setLabelModel(contract.getCar().getModel());
        setLabelYear(contract.getCar().getYearOfProduction());
        setLabelMileage(String.valueOf(contract.getCar().getMileAge()));
        setLabelFuel(contract.getCar().getFuel());
        setLabelEngineCapacity(String.valueOf(contract.getCar().getEngineCapacity()));
        setLabelEnginePower(String.valueOf(contract.getCar().getEnginePower()));
        setLabelGearBox(contract.getCar().getGearBox());
        setLabelBodyStyle(contract.getCar().getBodyStyle());
        setLabelColor(contract.getCar().getColor());
        setLabelPrice(contract.getCar().getPricePerDay() + "€");

        // add total price
        setLabelTotalPrice(Float.valueOf(contract.getPrice().toString()));

        setLabelFirstName(contract.getCustomer().getFirstName());
        setLabelLastName(contract.getCustomer().getLastName());
        setLabelCustomerId(contract.getCustomer().getCustomerID());

        setLabelAdress(contract.getCustomer().getAddress());
        setLabelBankAccount(contract.getCustomer().getBankAccount());
        setLabelCustomerPhone(contract.getCustomer().getPhone());
    }

    public void setOpenedAsDetail(Boolean openedAsDetail) {
        isOpenedAsDetail = openedAsDetail;

        buttonBack.setVisible(false);
        buttonCreateContract.setVisible(false);
    }

    public void setDateFROM(Date dateFROM) {
        this.dateFROM = dateFROM;
    }

    public void setDateTO(Date dateTO) {
        this.dateTO = dateTO;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    // SO MUCH SETTERS FOR LABELS IN GUI

    public void setLabelFirstName(String labelFirstName) {
        this.labelFirstName.setText(labelFirstName);
    }

    public void setLabelLastName(String labelLastName) {
        this.labelLastName.setText(labelLastName);
    }

    public void setLabelCustomerId(String labelCustomerId) {
        this.labelCustomerId.setText(labelCustomerId);
    }

    public void setLabelAdress(String labelAdress) {
        this.labelAdress.setText(labelAdress);
    }

    public void setLabelBankAccount(String labelBankAccount) {
        this.labelBankAccount.setText(labelBankAccount);
    }

    public void setLabelCustomerPhone(String labelCustomerPhone) {
        this.labelCustomerPhone.setText(labelCustomerPhone);
    }

    public void setLabelVIN(String labelVIN) {
        this.labelVIN.setText(labelVIN);
    }

    public void setLabelSPZ(String labelSPZ) {
        this.labelSPZ.setText(labelSPZ);
    }

    public void setLabelBrand(String labelBrand) {
        this.labelBrand.setText(labelBrand);
    }

    public void setLabelModel(String labelModel) {
        this.labelModel.setText(labelModel);
    }

    public void setLabelYear(String labelYear) {
        this.labelYear.setText(labelYear);
    }

    public void setLabelMileage(String labelMileage) {
        this.labelMileage.setText(labelMileage);
    }

    public void setLabelFuel(String labelFuel) {
        this.labelFuel.setText(labelFuel);
    }

    public void setLabelEngineCapacity(String labelEngineCapacity) {
        this.labelEngineCapacity.setText(labelEngineCapacity);
    }

    public void setLabelEnginePower(String labelEnginePower) {
        this.labelEnginePower.setText(labelEnginePower);
    }

    public void setLabelGearBox(String labelGearBox) {
        this.labelGearBox.setText(labelGearBox);
    }

    public void setLabelBodyStyle(String labelBodyStyle) {
        this.labelBodyStyle.setText(labelBodyStyle);
    }

    public void setLabelColor(String labelColor) {
        this.labelColor.setText(labelColor);
    }

    public void setLabelPrice(String labelPrice) {
        this.labelPrice.setText(labelPrice);
    }

    public void setLabelEmployeeFirstName(String labelEmployeeFirstName) {
        this.labelEmployeeFirstName.setText(labelEmployeeFirstName);
    }

    public void setLabelEmployeeLastName(String labelEmployeeLastName) {
        this.labelEmployeeLastName.setText(labelEmployeeLastName);
    }

    public void setLabelDateFromTo(String labelDateFromTo) {
        this.labelDateFromTo.setText(labelDateFromTo);
    }

    public void setLabelDateOfContract(String labelDateOfContract) {
        this.labelDateOfContract.setText(labelDateOfContract);
    }

    public void setLabelTotalPrice(Float labelTotalPrice) {
        this.labelTotalPrice.setText(String.valueOf(labelTotalPrice) + "€");
    }

    /**
     * Connect to server and new agreement record will be cerated and added to database.
     * @param actionEvent
     */
    public void btnSubmitPushed(ActionEvent actionEvent) {
        String resourceURL = "http://localhost:8080/api/contract/";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        //headers.set("Content-Type","application/json");
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<Contract> entity = new HttpEntity<>(contract,headers);

        ResponseEntity<Boolean> result;

        try {
            result = restTemplate.exchange(resourceURL, HttpMethod.POST, entity, Boolean.class);
        } catch (Exception e) {
            //LOG.log(Level.WARNING, actualLanguage.getString("notificationNoResponseServer"));
            showError(actualLanguage.getString("notificationNoResponseServer"));
            return;
        }

        showConfirm(actualLanguage.getString("notificationAdd"));

        backToMenu(rootPane,employee,actualLanguage);
    }

    /**
     * Calls another method called backToMenu().
     * @param actionEvent
     */
    public void btnBackPushed(ActionEvent actionEvent) {
        Parent parent = null;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/employee/employee_create_contract.fxml"),
                    actualLanguage);
            parent = (Parent) loader.load();

            EmployeeCreateContractController employeeCreateContractController = loader.getController();
            employeeCreateContractController.setEmployee(employee);
            employeeCreateContractController.setCar(contract.getCar());
            employeeCreateContractController.setCustomer(contract.getCustomer());

            employeeCreateContractController.setCarVin();
            employeeCreateContractController.setCustomerID();

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
