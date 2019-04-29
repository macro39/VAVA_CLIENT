package src.controller.employee;

import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXRadioButton;
import javafx.application.Platform;
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
import src.model.Contract;
import src.model.Employee;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.function.Predicate;
import java.util.logging.Level;

public class EmployeeSearchContractController extends EmployeeBackToMenu implements Initializable {
    

    @FXML private AnchorPane rootPane;
    @FXML private Label labelFirstName;
    @FXML private Label labelDate;
    @FXML private Label labelLastName;

    @FXML private TableView<Contract> tableView;
    @FXML private TableColumn<Contract, Integer> collumnNumberOfContract;
    @FXML private TableColumn<Contract, String> collumnID;
    @FXML private TableColumn<Contract, Date> collumnDateOfCreating;
    @FXML private TableColumn<Contract, String> collumnVIN;

    @FXML private TextField textFieldSearchInTable;

    @FXML private TextField textFieldSearchInDatabase;
    @FXML private JFXRadioButton radioButton2;
    @FXML private JFXProgressBar progressBar;

    @FXML private Button buttonPreviousData;
    @FXML private Button buttonNextData;
    @FXML private Label labelOffset;

    @FXML private JFXRadioButton radioButton1;

    private Employee employee;

    private Integer offSet = 0;

    private Boolean isButtonSearchInDatabasePushed = false;

    private ResourceBundle actualLanguage;

    private ObservableList<Contract> observableList;

    private FilteredList filteredList;

    private Boolean isButtonSearchEmployeeContractsPushed = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        actualLanguage = resources;

        collumnNumberOfContract.setCellValueFactory(new PropertyValueFactory<>("contractID"));
        collumnID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        collumnDateOfCreating.setCellValueFactory(new PropertyValueFactory<>("dateOfCreating"));
        collumnVIN.setCellValueFactory(new PropertyValueFactory<>("carVIN"));

        // enable funcionality of ordering the collumns by click on it
        collumnVIN.setSortType(TableColumn.SortType.ASCENDING);
        collumnNumberOfContract.setSortType(TableColumn.SortType.ASCENDING);
        collumnDateOfCreating.setSortType(TableColumn.SortType.ASCENDING);
        collumnID.setSortType(TableColumn.SortType.ASCENDING);

        tableView.getSortOrder().add(collumnVIN);
        tableView.getSortOrder().add(collumnDateOfCreating);
        tableView.getSortOrder().add(collumnID);
        tableView.getSortOrder().add(collumnNumberOfContract);

        buttonNextData.setDisable(true);
        buttonPreviousData.setDisable(true);

        ToggleGroup group = new ToggleGroup();

        radioButton1.setSelected(true);
        radioButton1.setToggleGroup(group);

        radioButton2.setSelected(false);
        radioButton2.setToggleGroup(group);

        progressBar.setVisible(false);
    }

    public void setHeader () {
        labelFirstName.setText(employee.getFirstName());
        labelLastName.setText(employee.getLastName());

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime());

        labelDate.setText(time);
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        setHeader();
    }

    public String getTextFieldSearchInDatabase() {
        return textFieldSearchInDatabase.getText();
    }

    public void setLabelOffset(String labelOffset) {
        this.labelOffset.setText(labelOffset);
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
        String resourceURL = "http://localhost:8080/api/contract/byOffSet/" + offSet;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<List<Contract>> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Contract>> result = restTemplate.exchange(resourceURL,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Contract>>() {
                });

        observableList = FXCollections.observableArrayList();

        observableList.addAll(result.getBody());
    }

    public void addItemsToListWithSpecification() {

        String resourceURL = "";

        if (radioButton1.isSelected()) {
            resourceURL = "http://localhost:8080/api/contract/byCar/" +
                    getTextFieldSearchInDatabase().toUpperCase();
        }

        if (radioButton2.isSelected()) {
            resourceURL = "http://localhost:8080/api/contract/byCustomer/" +
                    getTextFieldSearchInDatabase().toUpperCase();
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<List<Contract>> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Contract>> result = restTemplate.exchange(resourceURL,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Contract>>() {
                });

        observableList = FXCollections.observableArrayList();

        observableList.addAll(result.getBody());
    }

    public void addItemsToListCreatedByEmployee() {
        String resourceURL = "http://localhost:8080/api/contract/byEmployee/" + employee.getEmployeeID();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<Employee> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Contract>> result = restTemplate.exchange(resourceURL,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Contract>>() {
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

        if (tableView.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        Parent detailWindow;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/employee/employee_contract_detail.fxml"),
                    actualLanguage);
            detailWindow = (Parent) loader.load();

            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Detail");
            stage.setScene(new Scene(detailWindow));

            Contract contract = tableView.getSelectionModel().getSelectedItem();

            EmployeeContractDetailController employeeContractDetailController = loader.getController();
            employeeContractDetailController.setEmployee(employee);
            employeeContractDetailController.setContract(contract);
            employeeContractDetailController.setOpenedAsDetail(true);

            Task setLabels = new Task() {
                @Override
                protected Object call() throws Exception {

                    progressBar.setVisible(true);

                    final CountDownLatch latch = new CountDownLatch(1);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                employeeContractDetailController.setAllLabels();
                            } finally {
                                latch.countDown();
                            }
                        }
                    });
                    latch.await();

                    return null;
                }
            };

            setLabels.setOnSucceeded(event -> {
                progressBar.setVisible(false);

                stage.show();
            });

            Thread thread = new Thread(setLabels);
            thread.setDaemon(true);
            thread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteMenuSelected(ActionEvent actionEvent) {
        if (tableView.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        String resourceURL = "http://localhost:8080/api/contract/";

        Contract selectedContract = tableView.getSelectionModel().getSelectedItem();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        //headers.set("Content-Type","application/json");
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<Contract> entity = new HttpEntity<>(selectedContract, headers);

        ResponseEntity<Boolean> result;

        try {
            result = restTemplate.exchange(resourceURL, HttpMethod.DELETE, entity, Boolean.class);
        } catch (Exception e) {
            //LOG.log(Level.WARNING, actualLanguage.getString("notificationNoResponseServer"));
            showError(actualLanguage.getString("notificationNoResponseServer"));
            return;
        }

        showConfirm(actualLanguage.getString("notificationDeleteNotSuccessfulRecord"));

        observableList.remove(selectedContract);
        tableView.refresh();
    }

    public void searchInTable(KeyEvent keyEvent) {
        if(observableList == null) {
            return;
        }

        textFieldSearchInTable.textProperty().addListener((observable, oldvalue, newValue) -> {
            filteredList.setPredicate((Predicate<? super Contract>) contract ->{
                if(newValue == null ||  newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                if(contract.getCarVIN().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if(contract.getCustomerID().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false;
            });
        });

        SortedList<Contract> sortedList = new SortedList<>(filteredList);
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
            protected ObservableList<Contract> call() throws Exception {
                progressBar.setVisible(true);

                if(isButtonSearchInDatabasePushed) {
                    addItemsToListWithSpecification();
                } else if(isButtonSearchEmployeeContractsPushed) {
                   addItemsToListCreatedByEmployee();
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
            protected ObservableList<Contract> call() throws Exception {

                progressBar.setVisible(true);

                if(isButtonSearchInDatabasePushed) {
                    addItemsToListWithSpecification();
                } else if(isButtonSearchEmployeeContractsPushed) {
                    addItemsToListCreatedByEmployee();
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
            protected ObservableList<Contract> call() {

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
        isButtonSearchEmployeeContractsPushed = false;

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

    public void buttonDisplayMyContractsPushed(ActionEvent actionEvent) {
        buttonPreviousData.setDisable(true);
        isButtonSearchEmployeeContractsPushed = true;
        offSet = 0;

        Task addDataFromDatabase = new Task() {
            @Override
            protected Object call() throws Exception {

                progressBar.setVisible(true);

                addItemsToListCreatedByEmployee();

                return observableList;
            }
        };

        addDataFromDatabase.setOnSucceeded(event1 -> {
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

    public void btnBackPushed(ActionEvent actionEvent) {
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

    public void radioButtonSelected(ActionEvent actionEvent) {
    }
}
