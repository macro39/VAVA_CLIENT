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
import src.model.Car;

import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;

public class EmployeeCarDetailController implements Initializable {

    // noneditable attributes
    @FXML
    private TableView<Car> tableNonEditable;
    @FXML private TableColumn<Car, String> collumnVIN;
    @FXML private TableColumn<Car, String> collumnYearOfProduction;
    @FXML private TableColumn<Car, String> collumnBrand;
    @FXML private TableColumn<Car, String> collumnModel;
    @FXML private TableColumn<Car, String> collumnBodyStyle;
    @FXML private TableColumn<Car, Double> collumnEngineCapacity;
    @FXML private TableColumn<Car, String> collumnGearBox;
    @FXML private TableColumn<Car, String> collumnsFuel;
    @FXML private TableColumn<Car, String> collumnColor;

    // editable attributes
    @FXML private TableView<Car> tableEditable;
    @FXML private TableColumn<Car, String> collumnSPZ;
    @FXML private TableColumn<Car, Integer> collumnMileAge;
    @FXML private TableColumn<Car, Integer> collumnEnginePower;
    @FXML private TableColumn<Car, Double> collumnPricePerDay;

    // service record table
//    @FXML private TableView<ServiceRecord> tableServiceRecords;
//    @FXML private TableColumn<ServiceRecord, String> collumnServisName;
//    @FXML private TableColumn<ServiceRecord, String> collumnServisLocation;
//    @FXML private TableColumn<ServiceRecord, String> collumnTypeOfRepair;
//    @FXML private TableColumn<ServiceRecord, Date> collumnDateOfService;
//    @FXML private TableColumn<ServiceRecord, Float> collumnsPriceOfService;

    @FXML private Label labelNumberOfServices;

    // field for adding new service record
    @FXML private JFXTextField textFieldServiceName;
    @FXML private JFXTextField textFieldTypeOfRepair;
    @FXML private JFXDatePicker datePickerDateOfService;
    @FXML private JFXTextField textFieldPriceOfService;

    private Car car;

    // name and loction of all services
    ObservableList<String> allServices = null;

    // type of repair
    ObservableList<String> allRepairs = null;

    private Boolean dataChanged = false;

    private ResourceBundle actualLanguage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        actualLanguage = resources;

        // set noneditable table collumns
        collumnVIN.setCellValueFactory(new PropertyValueFactory<>("carVIN"));
        collumnYearOfProduction.setCellValueFactory(new PropertyValueFactory<>("yearOfProduction"));
        collumnBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        collumnModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        collumnBodyStyle.setCellValueFactory(new PropertyValueFactory<>("bodyStyle"));
        collumnEngineCapacity.setCellValueFactory(new PropertyValueFactory<>("engineCapacity"));
        collumnGearBox.setCellValueFactory(new PropertyValueFactory<>("gearBox"));
        collumnsFuel.setCellValueFactory(new PropertyValueFactory<>("fuel"));
        collumnColor.setCellValueFactory(new PropertyValueFactory<>("color"));

        // set editable table collumns
        collumnSPZ.setCellValueFactory(new PropertyValueFactory<>("carSPZ"));
        collumnMileAge.setCellValueFactory(new PropertyValueFactory<>("mileAge"));
        collumnEnginePower.setCellValueFactory(new PropertyValueFactory<>("enginePower"));
        collumnPricePerDay.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));

        tableEditable.setEditable(true);
        collumnSPZ.setCellFactory(TextFieldTableCell.forTableColumn());
        collumnMileAge.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        collumnEnginePower.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        collumnPricePerDay.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        // set service records table collumns
//        collumnServisName.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
//        collumnServisLocation.setCellValueFactory(new PropertyValueFactory<>("serviceLocation"));
//        collumnTypeOfRepair.setCellValueFactory(new PropertyValueFactory<>("typeOfRepair"));
//        collumnDateOfService.setCellValueFactory(new PropertyValueFactory<>("dateOfService"));
//        collumnsPriceOfService.setCellValueFactory(new PropertyValueFactory<>("servicePrice"));
    }

    public void setCar(Car car) {
        this.car = car;

        tableNonEditable.setItems(FXCollections.observableArrayList(car));
        tableEditable.setItems(FXCollections.observableArrayList(car));
//        tableServiceRecords.setItems(car.getServiceRecords());

        setServiceNamesAndLocations();
        setRepairTypes();
        setNumberOfServices();
    }

    public void setServiceNamesAndLocations() {
//        EnumManager enumManager = new EnumManager();
//        allServices = enumManager.getServiceNamesAndLocations();
//
//        TextFields.bindAutoCompletion(textFieldServiceName,allServices);
    }

    public void setRepairTypes() {
//        EnumManager enumManager = new EnumManager();
//        allRepairs = enumManager.getTypeOfHarm();
//
//        TextFields.bindAutoCompletion(textFieldTypeOfRepair,allRepairs);
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
//        labelNumberOfServices.setText("Počet záznamov: " + car.getServiceRecords().size());
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
    }
}
