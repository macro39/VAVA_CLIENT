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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class EmployeeSearchCustomerController extends EmployeeBackToMenu implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML private Label labelFirstName;
    @FXML private Label labelDate;
    @FXML private Label labelLastName;


    @FXML private TableView<Customer> tableView;
    @FXML private TableColumn<Customer, String> columnFirstName;
    @FXML private TableColumn<Customer, String> columnLastName;
    @FXML private TableColumn<Customer, String> columnID;
    @FXML private TableColumn<Customer, String> columnPhone;

    @FXML private Button buttonNextData;
    @FXML private Button buttonPreviousData;
    @FXML private TextField textFieldSearchInTable;
    @FXML private TextField textFieldSearchInDatabase;
    @FXML private Label labelOffset;
    @FXML private JFXProgressBar progressBar;

    private ObservableList<Customer> observableList;

    private FilteredList filteredList;

    private Employee employee;

    private Car car = null;
    private String carVIN = null;

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

        columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        columnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        columnID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // enable funcionality of ordering the collumns by click on it
        columnFirstName.setSortType(TableColumn.SortType.ASCENDING);
        columnLastName.setSortType(TableColumn.SortType.ASCENDING);
        columnID.setSortType(TableColumn.SortType.ASCENDING);
        columnPhone.setSortType(TableColumn.SortType.ASCENDING);

        tableView.getSortOrder().add(columnFirstName);
        tableView.getSortOrder().add(columnLastName);
        tableView.getSortOrder().add(columnID);
        tableView.getSortOrder().add(columnPhone);

        buttonNextData.setDisable(true);
        buttonPreviousData.setDisable(true);

        progressBar.setVisible(false);
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public void setCarVIN(String carVIN) {
       this.carVIN = carVIN;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        setHeader();
    }

    public void setOpenedFromContractScene(Boolean openedFromContractScene) {
        this.openedFromContractScene = openedFromContractScene;
    }

    public String getTextFieldSearchInDatabase() {
        return textFieldSearchInDatabase.getText();
    }

    public String getTextFieldSearchInDatabaseLegal() {
        return textFieldSearchInDatabase.getText();
    }

    public void setLabelOffset(String labelOffsetNatural) {
        this.labelOffset.setText(labelOffsetNatural);
    }

    public void setNewRangeOfDisplayedData() {
        int range = offSet*500 + observableList.size();

        if(range == 0) {
            setLabelOffset(0 + " - " + range);
        } else {
            setLabelOffset(((offSet*500 + 1) + " - " + range));
        }
    }

    public void addItemsToList() {
        String resourceURL = "http://localhost:8080/api/customer/byOffSet/" + offSet;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<List<Customer>> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Customer>> result = restTemplate.exchange(resourceURL,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Customer>>() {
                });

        observableList = FXCollections.observableArrayList();

        observableList.addAll(result.getBody());
    }

    public void addItemsToListWithSpecification() {

        String resourceURL = "http://localhost:8080/api/customer/searchByID/" +
                getTextFieldSearchInDatabase() + "/" + offSet;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<List<Customer>> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Customer>> result = restTemplate.exchange(resourceURL,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Customer>>() {
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

    public void detailMenuSelected(ActionEvent actionEvent) {
        if(tableView.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/employee/employee_customer_detail.fxml"), actualLanguage);

        EmployeeCustomerDetailController employeeCustomerDetailController;

        try {
            Parent detailWindow = (Parent) loader.load();

            employeeCustomerDetailController = loader.getController();
            Customer customerDetail = tableView.getSelectionModel().getSelectedItem();
            employeeCustomerDetailController.setNaturalPerson(customerDetail);

            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Detail");
            stage.setScene(new Scene(detailWindow));

            // on hiding, there is a functionality to add changes into database, if they were set
            stage.setOnHiding(event -> {
                Task updateInfo = new Task() {
                    @Override
                    protected Object call() {

                        if(employeeCustomerDetailController.getDataChanged()) {

                            String resourceURL = "http://localhost:8080/api/customer/";

                            RestTemplate restTemplate = new RestTemplate();

                            HttpHeaders headers = new HttpHeaders();
                            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                            HttpEntity<Customer> entity = new HttpEntity<>(customerDetail, headers);

                            restTemplate.exchange(resourceURL, HttpMethod.PUT, entity, Boolean.class);

                        }
                        return null;
                    }
                };

                updateInfo.setOnSucceeded(event1 -> {
                    if(employeeCustomerDetailController.getDataChanged()) {
                        showConfirm("Informácie boli úspešne aktualizované!");
                    }

                    tableView.refresh();
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

        Customer selectedCustomer = tableView.getSelectionModel().getSelectedItem();

        String resourceURL = "http://localhost:8080/api//customer/" + selectedCustomer.getCustomerID();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<Boolean> entity = new HttpEntity<>(headers);

        ResponseEntity<Boolean> result = restTemplate.exchange(resourceURL, HttpMethod.DELETE, entity, Boolean.class);

        if(result.getBody()) {
            showConfirm("Záznam bol úspešne odstránený!");

            tableView.getItems().remove(selectedCustomer);

            setNewRangeOfDisplayedData();
        } else {
            showError("Záznam sa nepodarilo odstrániť!");
        }
    }

    public void createContractMenuSelected(ActionEvent actionEvent) {
        if(tableView.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        backToCreateContract(true);
    }


    public void backToCreateContract(Boolean isPersonSelected) {
        Parent parent = null;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/employee/employee_create_contract.fxml"),actualLanguage);
            parent = (Parent) loader.load();

            EmployeeCreateContractController employeeCreateContractController= loader.getController();
            employeeCreateContractController.setEmployee(employee);

            if(isPersonSelected) {
                employeeCreateContractController.setCustomer(tableView.getSelectionModel().getSelectedItem());
            }

            if(car != null) {
                employeeCreateContractController.setCar(car);
                employeeCreateContractController.setCarVin();
            } else if(carVIN != null) {
                employeeCreateContractController.setSelectedCarVIN(carVIN);
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

    public void searchInTable(KeyEvent keyEvent) {

        if(observableList == null) {
            return;
        }

        textFieldSearchInTable.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate((Predicate<? super Customer>) customer ->{

                if(newValue == null ||  newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFiler = newValue.toLowerCase();
                if(customer.getFirstName().toLowerCase().contains(lowerCaseFiler)) {
                    return true;
                } else if(customer.getLastName().toLowerCase().contains(lowerCaseFiler)) {
                    return true;
                } else if(customer.getCustomerID().toLowerCase().contains(lowerCaseFiler)) {
                    return true;
                } else if(customer.getPhone().toLowerCase().contains(lowerCaseFiler)) {
                    return true;
                }

                return false;
            });
        });

        SortedList<Customer> sortedList = new SortedList<>(filteredList);
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
            protected ObservableList<Customer> call() throws Exception {
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
            protected ObservableList<Customer> call() throws Exception {

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
            showError("Zadaj údaj pre vyhľadávanie!");
            return;
        }

        buttonPreviousData.setDisable(true);
        isButtonSearchInDatabasePushed = true;
        offSet = 0;

        Task addDataFromDatabase = new Task() {
            @Override
            protected ObservableList<Customer> call() {

                progressBar.setVisible(true);

                addItemsToListWithSpecification();

                return observableList;
            }
        };

        addDataFromDatabase.setOnSucceeded(event -> {
            tableView.setItems(observableList);

            progressBar.setVisible(false);

            if(observableList.size() == 500) {

                showInformation("Počet nájdených záznamov je väčší ako " + observableList.size() + ".");

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
        if(openedFromContractScene) {
            backToCreateContract(false);
            return;
        }

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
}
