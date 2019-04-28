package src.controller.employee;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.controlsfx.control.textfield.TextFields;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import src.controller.Notification;
import src.model.Car;
import src.model.CarRepair;

import java.net.URL;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class EmployeeCarDetailController extends Notification implements Initializable {

    // noneditable attributes
    @FXML
    private TableView<Car> tableNonEditable;
    @FXML private TableColumn<Car, String> columnVIN;
    @FXML private TableColumn<Car, String> columnYearOfProduction;
    @FXML private TableColumn<Car, String> columnBrand;
    @FXML private TableColumn<Car, String> columnModel;
    @FXML private TableColumn<Car, String> columnBodyStyle;
    @FXML private TableColumn<Car, Double> columnEngineCapacity;
    @FXML private TableColumn<Car, String> columnGearBox;
    @FXML private TableColumn<Car, String> columnFuel;
    @FXML private TableColumn<Car, String> columnColor;

    // editable attributes
    @FXML private TableView<Car> tableEditable;
    @FXML private TableColumn<Car, String> columnSPZ;
    @FXML private TableColumn<Car, Integer> columnMileAge;
    @FXML private TableColumn<Car, Integer> columnEnginePower;
    @FXML private TableColumn<Car, Double> columnPricePerDay;

    // service record table
    @FXML private TableView<CarRepair> tableServiceRecords;
    @FXML private TableColumn<CarRepair, String> columnServiceName;
    @FXML private TableColumn<CarRepair, String> columnServiceLocation;
    @FXML private TableColumn<CarRepair, String> columnTypeOfRepair;
    @FXML private TableColumn<CarRepair, Date> columnDateOfService;
    @FXML private TableColumn<CarRepair, Float> columnPriceOfService;

    @FXML private Label labelNumberOfServices;

    // field for adding new service record
    @FXML private JFXTextField textFieldServiceName;
    @FXML private JFXTextField textFieldTypeOfRepair;
    @FXML private JFXDatePicker datePickerDateOfService;
    @FXML private JFXTextField textFieldPriceOfService;

    private Car car;

    // name and loction of all services
    ObservableList<String> allServices = null;

    private Boolean dataChanged = false;

    private ResourceBundle actualLanguage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        actualLanguage = resources;

        // set noneditable table collumns
        columnVIN.setCellValueFactory(new PropertyValueFactory<>("carVIN"));
        columnYearOfProduction.setCellValueFactory(new PropertyValueFactory<>("yearOfProduction"));
        columnBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        columnModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        columnBodyStyle.setCellValueFactory(new PropertyValueFactory<>("bodyStyle"));
        columnEngineCapacity.setCellValueFactory(new PropertyValueFactory<>("engineCapacity"));
        columnGearBox.setCellValueFactory(new PropertyValueFactory<>("gearBox"));
        columnFuel.setCellValueFactory(new PropertyValueFactory<>("fuel"));
        columnColor.setCellValueFactory(new PropertyValueFactory<>("color"));

        // set editable table collumns
        columnSPZ.setCellValueFactory(new PropertyValueFactory<>("carSPZ"));
        columnMileAge.setCellValueFactory(new PropertyValueFactory<>("mileAge"));
        columnEnginePower.setCellValueFactory(new PropertyValueFactory<>("enginePower"));
        columnPricePerDay.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));

        tableEditable.setEditable(true);
        columnSPZ.setCellFactory(TextFieldTableCell.forTableColumn());
        columnMileAge.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        columnEnginePower.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        columnPricePerDay.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        // set service records table collumns
        columnServiceName.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        columnServiceLocation.setCellValueFactory(new PropertyValueFactory<>("serviceLocation"));
        columnTypeOfRepair.setCellValueFactory(new PropertyValueFactory<>("typeOfRepair"));
        columnDateOfService.setCellValueFactory(new PropertyValueFactory<>("dateOfService"));
        columnPriceOfService.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    public void setCar(Car car) {
        this.car = car;

        tableNonEditable.setItems(FXCollections.observableArrayList(car));
        tableEditable.setItems(FXCollections.observableArrayList(car));
        tableServiceRecords.setItems(FXCollections.observableArrayList(car.getCarRepairs()));

        setServiceNamesAndLocations();
        setNumberOfServices();
    }

    public void setServiceNamesAndLocations() {

        String resourceURL = "http://localhost:8080/api/getServices/";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        //headers.set("Content-Type","application/json");
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<List<String>> entity = new HttpEntity<>(headers);

        ResponseEntity<List<String>> result;

        try {
            result = restTemplate.exchange(resourceURL, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<List<String>>() {
            });
        } catch (Exception e) {
            //LOG.log(Level.WARNING, actualLanguage.getString("notificationNoResponseServer"));
            showError(actualLanguage.getString("notificationNoResponseServer"));
            return;
        }

        allServices = FXCollections.observableArrayList(result.getBody());

        TextFields.bindAutoCompletion(textFieldServiceName,allServices);
    }

    public void setEditation(Boolean editationEnabled) {
        tableEditable.setEditable(editationEnabled);
    }

    // if data were changed
    public Boolean getDataChanged() {
        return dataChanged;
    }

    public String getServiceName() {
        return textFieldServiceName.getText();
    }

    public String getTypeOfRepair() { return textFieldTypeOfRepair.getText(); }

    public Date getDateOfService() {
        if(datePickerDateOfService.getValue() == null) {
            return null;
        }
        java.sql.Date date = java.sql.Date.valueOf(datePickerDateOfService.getValue());
        return date;
    }

    public Float getPriceOfService() {
        return Float.parseFloat(textFieldPriceOfService.getText());
    }

    public void removeTypedInfo() {
        textFieldServiceName.setText("");
        textFieldTypeOfRepair.setText("");
        datePickerDateOfService.getEditor().clear();
        textFieldPriceOfService.setText("");
    }

    public void setNumberOfServices() {
        labelNumberOfServices.setText("Počet záznamov: " + car.getCarRepairs().size());
    }

    public Boolean checkFieldsBeforeSubmittingNewRepair() {
        if(getServiceName().trim().isEmpty() ||
                getTypeOfRepair().trim().isEmpty() ||
                getDateOfService() == null) {
            return true;
        } else {
            try {
                Float.parseFloat(getPriceOfService().toString());
            } catch (NumberFormatException e) {
                return true;
            }
        }
        return false;
    }

    public void changeSPZCell(TableColumn.CellEditEvent<Car, String> carStringCellEditEvent) {
        dataChanged = true;
        car.setCarSPZ(carStringCellEditEvent.getNewValue().toUpperCase());
    }

    public void changeMileAgeCell(TableColumn.CellEditEvent<Car, Integer> carIntegerCellEditEvent) {
        dataChanged = true;
        car.setMileAge(carIntegerCellEditEvent.getNewValue());
    }

    public void changeEnginePowerCell(TableColumn.CellEditEvent<Car, Integer> carIntegerCellEditEvent) {
        dataChanged = true;
        car.getCarInfo().setEnginePower(carIntegerCellEditEvent.getNewValue());
    }

    public void changePricePerDayCell(TableColumn.CellEditEvent<Car, Double> carDoubleCellEditEvent) {
        dataChanged = true;
        car.getCarInfo().setPricePerDay(carDoubleCellEditEvent.getNewValue());
    }

    public void buttonAddNewServiceRecord(ActionEvent actionEvent) {
        if(checkFieldsBeforeSubmittingNewRepair() || !allServices.contains(getServiceName())) {
            showError("Nesprávne vyplnené údaje!");
        } else {

            String nameAndLocation[] = getServiceName().split(", ");

            car.addRepair(new CarRepair(getTypeOfRepair(),getDateOfService(),
                    getPriceOfService(),nameAndLocation[0],nameAndLocation[1]));

            String resourceURL = "http://localhost:8080/api/addRepair/";

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            //headers.set("Content-Type","application/json");
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<Car> entity = new HttpEntity<>(car,headers);

            ResponseEntity<Boolean> result;

            try {
                result = restTemplate.exchange(resourceURL, HttpMethod.POST, entity, Boolean.class);
            } catch (Exception e) {
                //LOG.log(Level.WARNING, actualLanguage.getString("notificationNoResponseServer"));
                showError(actualLanguage.getString("notificationNoResponseServer"));
                return;
            }

            showConfirm("Záznam o servisovaní bol úspešne pridaný!");

            removeTypedInfo();

            tableServiceRecords.setItems(FXCollections.observableArrayList(car.getCarRepairs()));
            setNumberOfServices();
        }
    }
}
