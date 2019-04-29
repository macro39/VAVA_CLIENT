package src.controller.employee;

import com.jfoenix.controls.JFXProgressBar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import src.model.Car;
import src.model.Customer;
import src.model.Employee;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;

public class EmployeeSearchCarController extends EmployeeBackToMenu implements Initializable {

    @FXML
    private AnchorPane rootPane;

    // header labels
    @FXML private Label labelFirstName;
    @FXML private Label labelLastName;
    @FXML private Label labelDate;

    // table view and columns
    @FXML private TableView<Car> tableView;
    @FXML private TableColumn<Car, String> columnVIN;
    @FXML private TableColumn<Car, String> columnSPZ;
    @FXML private TableColumn<Car, String> columnYearOfProduction;
    @FXML private TableColumn<Car, Integer> columnMileage;

    // textfields for search
    @FXML private TextField textFieldSearchInTable;
    @FXML private TextField textFieldSearchInDatabase;

    private ObservableList<Car> observableList;

    private FilteredList filteredList;

    // for load next and previous data
    @FXML private Button buttonNextData;
    @FXML private Button buttonPreviousData;
    @FXML private Label labelOffset;

    @FXML private JFXProgressBar progressBar;

    private Employee employee;

    private Customer customer = null;
    private String customerID = null;

    private Integer offSet = 0;

    private Boolean isButtonSearchInDatabasePushed = false;

    private Boolean openedFromContractScene = false;

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

        columnVIN.setCellValueFactory(new PropertyValueFactory<>("carVIN"));
        columnSPZ.setCellValueFactory(new PropertyValueFactory<>("carSPZ"));
        columnYearOfProduction.setCellValueFactory(new PropertyValueFactory<>("yearOfProduction"));
        columnMileage.setCellValueFactory(new PropertyValueFactory<>("mileAge"));

        // enable funcionality of ordering the collumns by click on it
        columnVIN.setSortType(TableColumn.SortType.ASCENDING);
        columnSPZ.setSortType(TableColumn.SortType.ASCENDING);
        columnYearOfProduction.setSortType(TableColumn.SortType.ASCENDING);
        columnMileage.setSortType(TableColumn.SortType.ASCENDING);

        tableView.getSortOrder().add(columnVIN);
        tableView.getSortOrder().add(columnSPZ);
        tableView.getSortOrder().add(columnYearOfProduction);
        tableView.getSortOrder().add(columnMileage);

        buttonNextData.setDisable(true);
        buttonPreviousData.setDisable(true);

        progressBar.setVisible(false);
    }

    public String getTextFieldSearchInDatabase() {
        return textFieldSearchInDatabase.getText();
    }

    public void setLabelOffset(String labelOffset) {
        this.labelOffset.setText(labelOffset);
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        setHeader();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public void setOpenedFromContractScene(Boolean openedFromContractScene) {
        this.openedFromContractScene = openedFromContractScene;
    }

    public void setNewRangeOfDisplayedData() {
        int range = offSet*500 + observableList.size();

        if(range == 0) {
            setLabelOffset(0 + " - " + range);
        } else {
            setLabelOffset(((offSet*500 + 1) + " - " + range));
        }
    }

    // add items

    public void addItemsToList() {
        String resourceURL = "http://localhost:8080/api/car/byOffSet/" + offSet;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<List<Car>> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Car>> result = restTemplate.exchange(resourceURL,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Car>>() {
                });

        observableList = FXCollections.observableArrayList();

        observableList.addAll(result.getBody());
    }

    public void addItemsToListWithSpecification() {

        String resourceURL = "http://localhost:8080/api/car/" +
                getTextFieldSearchInDatabase() + "/" + offSet;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<List<Car>> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Car>> result = restTemplate.exchange(resourceURL,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Car>>() {
                });

        observableList = FXCollections.observableArrayList();

        observableList.addAll(result.getBody());
    }

    public void addItemsToTable() {
        tableView.setItems(observableList);

        filteredList = new FilteredList(observableList,e->true);

        if(observableList.size() == 500) {
            buttonNextData.setDisable(false);
        }
    }

    // menu

    public void detailMenuSelected(ActionEvent actionEvent) {
        if(tableView.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/employee/employee_car_detail.fxml"), actualLanguage);

        EmployeeCarDetailController employeeCarDetailController;

        try {
            Parent detailWindow = (Parent) loader.load();

            employeeCarDetailController = loader.getController();
            Car selectedCar = tableView.getSelectionModel().getSelectedItem();

            employeeCarDetailController.setCar(selectedCar);

            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Detail");
            stage.setScene(new Scene(detailWindow));

            // on hiding, there is a funcionality to add changes into database, if they were set
            stage.setOnHiding(event1 -> {
                Task updateInfo = new Task() {
                    @Override
                    protected Object call() throws Exception {

                        if(employeeCarDetailController.getDataChanged()) {

                            String resourceURL = "http://localhost:8080/api/car";

                            RestTemplate restTemplate = new RestTemplate();

                            HttpHeaders headers = new HttpHeaders();
                            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                            HttpEntity<Car> entity = new HttpEntity<>(selectedCar, headers);

                            restTemplate.exchange(resourceURL, HttpMethod.PUT, entity, Boolean.class);

                            tableView.refresh();            // if changes were set, this will update table
                        }

                        return null;
                    }
                };

                updateInfo.setOnSucceeded(event2 -> {
                    if(employeeCarDetailController.getDataChanged()) {
                        showConfirm(actualLanguage.getString("notificationUpdateRecord"));
                    }
                });

                Thread thread = new Thread(updateInfo);
                thread.setDaemon(true);
                thread.start();
            });

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteMenuSelected(ActionEvent actionEvent) {
        if(tableView.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        Car selectedCar = tableView.getSelectionModel().getSelectedItem();

        String resourceURL = "http://localhost:8080/api/car/" + selectedCar.getCarVIN();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<Boolean> entity = new HttpEntity<>(headers);

        ResponseEntity<Boolean> result = restTemplate.exchange(resourceURL, HttpMethod.DELETE, entity, Boolean.class);

        if(result.getBody()) {
            showConfirm(actualLanguage.getString("notificationDeleteRecord"));

            tableView.getItems().remove(selectedCar);

            setNewRangeOfDisplayedData();
        } else {
            showError(actualLanguage.getString("notificationDeleteNotSuccessfulRecord"));
        }
    }

    public void createContractMenuSelected(ActionEvent actionEvent) {
        if(tableView.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        backToCreateContract(true);
    }

    public void searchInTable(KeyEvent keyEvent) {
        if(observableList == null) {
            return;
        }

        textFieldSearchInTable.textProperty().addListener((observable, oldvalue, newValue) -> {
            filteredList.setPredicate((Predicate<? super Car>) car ->{
                if(newValue == null ||  newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                if(car.getCarVIN().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if(car.getCarSPZ().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false;
            });
        });

        SortedList<Car> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);

        setNewRangeOfDisplayedData();
    }

    public void loadNext(ActionEvent actionEvent) {
        offSet++;

        if(offSet > 0) {
            buttonPreviousData.setDisable(false);
        }

        Task loadNext = new Task() {
            @Override
            protected ObservableList<Car> call() throws Exception {
                progressBar.setVisible(true);

                if(isButtonSearchInDatabasePushed) {
                    addItemsToListWithSpecification();
                } else {
                    addItemsToList();
                }

                return observableList;
            }
        };

        loadNext.setOnSucceeded(event -> {
            tableView.setItems(observableList);

            progressBar.setVisible(false);

            if(observableList.size() < 500) {
                buttonNextData.setDisable(true);
            }

            filteredList = new FilteredList(observableList,e->true);

            textFieldSearchInTable.setText("");

            setNewRangeOfDisplayedData();
        });

        Thread thread = new Thread(loadNext);
        thread.setDaemon(true);
        thread.start();
    }

    public void loadPrevious(ActionEvent actionEvent) {
        if(offSet > 0) {
            buttonNextData.setDisable(false);
        }

        offSet--;

        if(offSet == 0) {
            buttonPreviousData.setDisable(true);
        }

        Task loadPrevious = new Task() {
            @Override
            protected ObservableList<Car> call() throws Exception {

                progressBar.setVisible(true);

                if(isButtonSearchInDatabasePushed) {
                    addItemsToListWithSpecification();
                } else {
                    addItemsToList();
                }

                return observableList;
            }
        };

        loadPrevious.setOnSucceeded(event -> {
            tableView.setItems(observableList);

            progressBar.setVisible(false);

            filteredList = new FilteredList(observableList,e->true);

            textFieldSearchInTable.setText("");

            setNewRangeOfDisplayedData();
        });

        Thread thread = new Thread(loadPrevious);
        thread.setDaemon(true);
        thread.start();
    }

    public void buttonSearchInDatabasePushed(ActionEvent actionEvent) {
        if(getTextFieldSearchInDatabase().trim().isEmpty()) {
            showError(actualLanguage.getString("notificationNoEnterData"));
            return;
        }

        buttonPreviousData.setDisable(true);
        isButtonSearchInDatabasePushed = true;
        offSet = 0;

        Task addDataFromDatabase = new Task() {
            @Override
            protected ObservableList<Car> call() {

                progressBar.setVisible(true);

                addItemsToListWithSpecification();

                return observableList;
            }
        };

        addDataFromDatabase.setOnSucceeded(event -> {
            tableView.setItems(observableList);

            progressBar.setVisible(false);

            if(observableList.size() == 500) {

                showInformation(actualLanguage.getString("notificationFoundItemsAreMoreThan") + observableList.size() + ".");

                buttonNextData.setDisable(false);
            } else {
                buttonNextData.setDisable(true);
            }

            filteredList = new FilteredList(observableList,e->true);

            textFieldSearchInTable.setText("");

            setNewRangeOfDisplayedData();
        });

        Thread thread = new Thread(addDataFromDatabase);
        thread.setDaemon(true);
        thread.start();
    }

    public void buttonDisplayDataPushed(ActionEvent actionEvent) {
        isButtonSearchInDatabasePushed = false;

        offSet = 0;

        buttonPreviousData.setDisable(true);

        Task displayData = new Task() {
            @Override
            protected Object call() {
                progressBar.setVisible(true);

                addItemsToList();

                return null;
            }
        };

        displayData.setOnSucceeded(event -> {
            addItemsToTable();

            filteredList = new FilteredList(observableList,e->true);

            progressBar.setVisible(false);

            textFieldSearchInTable.setText("");

            if(observableList.size() == 500) {
                buttonNextData.setDisable(false);
            }

            setNewRangeOfDisplayedData();
        });

        Thread thread = new Thread(displayData);
        thread.setDaemon(true);
        thread.start();
    }

    public void btnBackPushed(ActionEvent actionEvent) {
        Parent parent = null;

        if(openedFromContractScene) {
            backToCreateContract(false);
            return;
        }

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

    public void backToCreateContract(Boolean isCarSelected) {
        Parent parent = null;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/employee/employee_create_contract.fxml"),actualLanguage);
            parent = (Parent) loader.load();

            EmployeeCreateContractController employeeCreateContractController = loader.getController();
            employeeCreateContractController.setEmployee(employee);

            if(isCarSelected) {
                Car selectedCar = tableView.getSelectionModel().getSelectedItem();

                employeeCreateContractController.setCar(selectedCar);
            }

            if(customer != null) {
                employeeCreateContractController.setCustomer(customer);
                employeeCreateContractController.setCustomerID();
            } else if (customerID != null) {
                employeeCreateContractController.setSelectedCustomerID(customerID);
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
}
