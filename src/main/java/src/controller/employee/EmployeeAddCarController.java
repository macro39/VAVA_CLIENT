package src.controller.employee;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import src.controller.LoginController;
import src.model.Car;
import src.model.CarInfo;
import src.model.Employee;

import java.net.URL;
import java.sql.Date;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmployeeAddCarController extends EmployeeBackToMenu implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML private JFXComboBox comboBoxBrand;
    @FXML private JFXComboBox comboBoxModel;

    @FXML private JFXDatePicker datePickerYear;
    @FXML private Spinner spinnerMileage;
    @FXML private JFXComboBox comboBoxFuel;
    @FXML private Spinner spinnerEngineCapacity;
    @FXML private Spinner spinnerEnginePower;
    @FXML private JFXComboBox comboBoxGearBox;
    @FXML private JFXComboBox comboBoxCarBody;
    @FXML private JFXComboBox comboBoxColor;

    @FXML private Button buttonAddColor;
    @FXML private Button buttonAddModel;

    @FXML private JFXTextField textFieldSPZ;
    @FXML private JFXTextField textFieldVIN;
    @FXML private JFXTextField textFieldPrice;

    private Employee employee;

    private ResourceBundle actualLanguage;

    private static Logger LOG = Logger.getLogger(LoginController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        actualLanguage = resources;

        spinnerConfig();
        addItemsComboBox();

        comboBoxModel.setDisable(true);
        buttonAddModel.setDisable(true);
        comboBoxCarBody.setDisable(true);
        comboBoxColor.setDisable(true);
        buttonAddColor.setDisable(true);
        comboBoxFuel.setDisable(true);
        comboBoxGearBox.setDisable(true);
        textFieldSPZ.setDisable(true);
        textFieldVIN.setDisable(true);
        textFieldPrice.setDisable(true);
        spinnerMileage.setDisable(true);
        spinnerEnginePower.setDisable(true);
        spinnerEngineCapacity.setDisable(true);
        datePickerYear.setDisable(true);
    }

    public void addItemsComboBox() {

        ComboBox[] array = new ComboBox[5];

        array[0] = comboBoxBrand;
        array[1] = comboBoxCarBody;
        array[2] = comboBoxGearBox;
        array[3] = comboBoxFuel;
        array[4] = comboBoxColor;

        String resourceURL = "http://localhost:8080/api/carInfo/getAll";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<ArrayList<String>[]> entity = new HttpEntity<>(headers);

        ResponseEntity<ArrayList<String>[]> result;

        try {
            result = restTemplate.exchange(resourceURL,
                    HttpMethod.GET, entity, new ParameterizedTypeReference<ArrayList<String>[]>() {
                    });
        } catch (Exception e) {
            LOG.log(Level.WARNING, actualLanguage.getString("notificationNoResponseServer"));
            showError(actualLanguage.getString("notificationNoResponseServer"));
            return;
        }

        for(int i = 0; i < 5; i++) {
            array[i].getItems().clear();
            array[i].getItems().addAll(result.getBody()[i]);
            array[i].setVisibleRowCount(array[i].getItems().size() - 1);
        }

    }

    public void spinnerConfig() {
        spinnerEngineCapacity.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(2.5,5.0,1,0.1));
        spinnerEnginePower.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(80,500,180,1));
        spinnerMileage.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,Integer.MAX_VALUE,10000,1000));
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    // getters for fields and spinners in gui
    public String getBrand() {
        if(comboBoxBrand.getSelectionModel().isEmpty()) {
            return null;
        }
        return comboBoxBrand.getSelectionModel().getSelectedItem().toString();
    }

    public String getModel() {
        if(comboBoxModel.getSelectionModel().isEmpty()) {
            return null;
        }
        return comboBoxModel.getSelectionModel().getSelectedItem().toString();
    }

    public Integer getMileAge() {
        return Integer.parseInt(spinnerMileage.getValue().toString());
    }

    public String getFuel() {
        if(comboBoxFuel.getSelectionModel().isEmpty()) {
            return null;
        }
        return comboBoxFuel.getSelectionModel().getSelectedItem().toString();
    }

    public String getGearBox() {
        if(comboBoxGearBox.getSelectionModel().isEmpty()) {
            return null;
        }
        return comboBoxGearBox.getSelectionModel().getSelectedItem().toString();
    }

    public String getCarBody() {
        if(comboBoxCarBody.getSelectionModel().isEmpty()) {
            return null;
        }
        return comboBoxCarBody.getSelectionModel().getSelectedItem().toString();
    }

    public String getColor() {
        if(comboBoxColor.getSelectionModel().isEmpty()) {
            return null;
        }
        return comboBoxColor.getSelectionModel().getSelectedItem().toString();
    }

    public Double getPrice() {
        return Double.parseDouble(textFieldPrice.getText());
    }

    public Date getYear() {
        if(datePickerYear.getValue() == null) {
            return null;
        }
        Date date = Date.valueOf(datePickerYear.getValue());
        return date;
    }

    public Double getEngineCapacity() {
        return Double.parseDouble(spinnerEngineCapacity.getValue().toString());
    }

    public Integer getEnginePower() {
        return Integer.parseInt(spinnerEnginePower.getValue().toString());
    }

    public String getSPZ() {
        return textFieldSPZ.getText();
    }

    public String getVIN() {
        return textFieldVIN.getText();
    }

    public boolean tooLongText() {
        if(getSPZ().length() > 20 ||
                getVIN().length() > 17){
            return true;
        }
        return false;
    }

    public boolean emptyFieldChecker() {
        if(getBrand() == null ||
                getModel() == null ||
                getFuel() == null ||
                getGearBox() == null ||
                getCarBody() == null ||
                getColor() == null ||
                getYear() == null ||
                getSPZ().trim().isEmpty() ||
                getVIN().trim().isEmpty()){
            return true;
        } else {
            try {
                Float.parseFloat(getPrice().toString());
            } catch (NumberFormatException e) {
                return true;
            }
        }
        return false;
    }

    public void btnConfirmPushed(ActionEvent actionEvent) {

        if(tooLongText()) {
            showWarning("Príliš dlhé údaje v kolonke ŠPZ/VIN! Max. počet znakov pre VIN je 17, pre ŠPZ 20.");
            return;
        }

        if(emptyFieldChecker()) {
            showWarning("Vypíšte správne všetky údaje!");
            return;
        }

        if(!getYear().before(Calendar.getInstance().getTime())){
            showError("Rok výroby auta je neplatný!");
            return;
        } else {
            CarInfo carInfo = new CarInfo(getBrand(),getModel(),getCarBody(),
                    getEngineCapacity(),getEnginePower(),getGearBox(),getFuel(),getColor(),getPrice());

            Car car = new Car(getVIN().toUpperCase(),carInfo,getYear(),getMileAge(),getSPZ().toUpperCase());

            String resourceURL = "http://localhost:8080/api/car";

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<Car> entity = new HttpEntity<>(car, headers);

            ResponseEntity<Boolean> result;

            try {
                result = restTemplate.exchange(resourceURL,
                        HttpMethod.POST, entity, Boolean.class);
            } catch (Exception e) {
                LOG.log(Level.WARNING, actualLanguage.getString("notificationNoResponseServer"));
                showError(actualLanguage.getString("notificationNoResponseServer"));
                return;
            }

            if(result.getBody()) {
                showConfirm("Auto s VIN číslom " + getVIN() + " bolo pridané!");
                backToMenu(rootPane,employee,actualLanguage);
            } else {
                showError("Záznam o aute s VIN číslom " + getVIN() + " už existuje!");
            }
        }

    }

    public void btnAddBrandPushed(ActionEvent actionEvent) {
    }

    public void btnAddModelPushed(ActionEvent actionEvent) {
    }

    public void btnAddColorPushed(ActionEvent actionEvent) {
    }

    public void comboBoxModelClicked(MouseEvent mouseEvent) {
        if(getBrand() == null) {
            return;
        } else {
            String resourceURL = "http://localhost:8080/api/carInfo/model/" + getBrand();

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            //headers.set("Content-Type","application/json");
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<Employee> entity = new HttpEntity<>(headers);

            ResponseEntity<List<String>> result;

            try {
                result = restTemplate.exchange(resourceURL, HttpMethod.GET, entity,
                        new ParameterizedTypeReference<List<String>>() {
                });
            } catch (Exception e) {
                LOG.log(Level.WARNING, actualLanguage.getString("notificationNoResponseServer"));
                showError(actualLanguage.getString("notificationNoResponseServer"));
                return;
            }

            comboBoxModel.getItems().clear();

            comboBoxModel.getItems().addAll(result.getBody());

            comboBoxModel.setVisibleRowCount(comboBoxModel.getItems().size() - 1);
        }
    }

    // selections

    public void comboBoxBrandSelected(ActionEvent actionEvent) {
        comboBoxModel.setDisable(false);
        buttonAddModel.setDisable(false);
    }

    public void comboBoxModelSelected(ActionEvent actionEvent) {
        datePickerYear.setDisable(false);
    }

    public void datePickerDateSelected(ActionEvent actionEvent) {
        spinnerMileage.setDisable(false);
        comboBoxFuel.setDisable(false);
    }

    public void comboBoxFuelSelected(ActionEvent actionEvent) {
        spinnerEnginePower.setDisable(false);
        spinnerEngineCapacity.setDisable(false);
        comboBoxGearBox.setDisable(false);
    }

    public void comboBoxGearBoxSelected(ActionEvent actionEvent) {
        comboBoxCarBody.setDisable(false);
    }

    public void comboBoxCarBodySelected(ActionEvent actionEvent) {
        comboBoxColor.setDisable(false);
        buttonAddColor.setDisable(false);
    }

    public void comboBoxColorSelected(ActionEvent actionEvent) {
        textFieldSPZ.setDisable(false);
        textFieldVIN.setDisable(false);
        textFieldPrice.setDisable(false);
    }

    public void btnBackPushed(ActionEvent actionEvent) {
        backToMenu(rootPane,employee,actualLanguage);
    }
}
