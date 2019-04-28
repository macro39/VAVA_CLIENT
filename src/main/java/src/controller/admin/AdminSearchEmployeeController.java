package src.controller.admin;

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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import src.controller.Notification;
import src.model.Employee;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;

public class AdminSearchEmployeeController extends Notification implements Initializable {

    @FXML
    private AnchorPane rootPane;

    // header labels
    @FXML private Label labelFirstName;
    @FXML private Label labelLastName;
    @FXML private Label labelDate;

    // table view and collumns
    @FXML private TableView<Employee> tableView;
    @FXML private TableColumn<Employee,String> columnFirstName;
    @FXML private TableColumn<Employee,String> columnLastName;
    @FXML private TableColumn<Employee,String> columnLogin;
    @FXML private TableColumn<Employee,String> columnPhone;

    // textfields for search
    @FXML private TextField textFieldSearchInTables;
    @FXML private TextField textFieldSearchInDatabase;

    private ObservableList<Employee> observableList;

    private FilteredList filer;

    @FXML private Button buttonNextData;
    @FXML private Button buttonPreviousData;
    @FXML private Label labelOffset;

    @FXML private JFXProgressBar progressBar;

    private Employee admin;

    private Integer offSet = 0;

    private Boolean isButtonSearchInDatabasePushed = false;

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

        columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        columnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        columnLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // enable funcionality of ordering the collumns by click on it
        columnFirstName.setSortType(TableColumn.SortType.ASCENDING);
        columnLastName.setSortType(TableColumn.SortType.ASCENDING);
        columnLogin.setSortType(TableColumn.SortType.ASCENDING);
        columnPhone.setSortType(TableColumn.SortType.ASCENDING);

        tableView.getSortOrder().add(columnFirstName);
        tableView.getSortOrder().add(columnLastName);
        tableView.getSortOrder().add(columnLogin);
        tableView.getSortOrder().add(columnPhone);

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

    public void setAdmin(Employee admin) {
        this.admin = admin;
        setHeader();
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
        String resourceURL = "http://localhost:8080/api/employee/byOffSet/" + offSet;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<List<Employee>> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Employee>> result = restTemplate.exchange(resourceURL,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Employee>>() {
        });

        observableList = FXCollections.observableArrayList();

        observableList.addAll(result.getBody());
    }

    public void addItemsToListWithSpecification() {

        String resourceURL = "http://localhost:8080/api/employee/searchByLastName/" +
                getTextFieldSearchInDatabase() + "/" + offSet;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<List<Employee>> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Employee>> result = restTemplate.exchange(resourceURL,
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Employee>>() {
        });

        observableList = FXCollections.observableArrayList();

        observableList.addAll(result.getBody());
    }

    public void addItemsToTable() {
        tableView.setItems(observableList);

        filer = new FilteredList(observableList,e->true);

        if(observableList.size() == 500) {
            buttonNextData.setDisable(false);
        }
    }

    public void searchInTable(KeyEvent keyEvent) {

        if(observableList == null) {
            return;
        }

        textFieldSearchInTables.textProperty().addListener((observable, oldValue, newValue) -> {
            filer.setPredicate((Predicate<? super Employee>) employee ->{

                if(newValue == null ||  newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFiler = newValue.toLowerCase();
                if(employee.getFirstName().toLowerCase().contains(lowerCaseFiler)) {
                    return true;
                } else if(employee.getLastName().toLowerCase().contains(lowerCaseFiler)) {
                    return true;
                } else if(employee.getLogin().toLowerCase().contains(lowerCaseFiler)) {
                    return true;
                } else if(employee.getPhone().toLowerCase().contains(lowerCaseFiler)) {
                    return true;
                }

                return false;
            });
        });

        SortedList<Employee> sortedList = new SortedList<>(filer);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);

        setNewRangeOfDisplayedData();
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
            protected ObservableList<Employee> call() {

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

            filer = new FilteredList(observableList,e->true);

            textFieldSearchInTables.setText("");

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

            filer = new FilteredList(observableList,e->true);

            progressBar.setVisible(false);

            textFieldSearchInTables.setText("");

            if(observableList.size() == 500) {
                buttonNextData.setDisable(false);
            }

            setNewRangeOfDisplayedData();
        });

        Thread thread = new Thread(displayData);
        thread.setDaemon(true);
        thread.start();
    }

    public void loadNext(ActionEvent actionEvent) {
        offSet++;

        if(offSet > 0) {
            buttonPreviousData.setDisable(false);
        }

        Task loadNext = new Task() {
            @Override
            protected ObservableList<Employee> call() throws Exception {
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

            filer = new FilteredList(observableList,e->true);

            textFieldSearchInTables.setText("");

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
            protected ObservableList<Employee> call() throws Exception {

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

            filer = new FilteredList(observableList,e->true);

            textFieldSearchInTables.setText("");

            setNewRangeOfDisplayedData();
        });

        Thread thread = new Thread(loadPrevious);
        thread.setDaemon(true);
        thread.start();
    }

    public void detailMenuSelected(ActionEvent actionEvent) {
        if(tableView.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/admin/admin_employee_detail.fxml"), actualLanguage);

        AdminEmployeeDetailController adminEmployeeDetailController;

        try {
            Parent detailWindow = (Parent) loader.load();

            adminEmployeeDetailController = loader.getController();
            Employee selectedEmployee = tableView.getSelectionModel().getSelectedItem();
            adminEmployeeDetailController.setEmployee(selectedEmployee);

            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Detail");
            stage.setScene(new Scene(detailWindow));

            // on hiding, there is a funcionality to add changes into database, if they were set
            stage.setOnHiding(event -> {
                Task updateInfo = new Task() {
                    @Override
                    protected Object call() {

                        if(adminEmployeeDetailController.getDataChanged()) {

                            String resourceURL = "http://localhost:8080/api/employee/";

                            RestTemplate restTemplate = new RestTemplate();

                            HttpHeaders headers = new HttpHeaders();
                            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                            HttpEntity<Employee> entity = new HttpEntity<>(selectedEmployee, headers);

                            restTemplate.exchange(resourceURL, HttpMethod.PUT, entity, Boolean.class);

                        }
                        return null;
                    }
                };

                updateInfo.setOnSucceeded(event1 -> {
                    if(adminEmployeeDetailController.getDataChanged()) {
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

        Employee selectedEmployee = tableView.getSelectionModel().getSelectedItem();

        String resourceURL = "http://localhost:8080/api//employee/" + selectedEmployee.getEmployeeID();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<Boolean> entity = new HttpEntity<>(headers);

        ResponseEntity<Boolean> result = restTemplate.exchange(resourceURL, HttpMethod.DELETE, entity, Boolean.class);

        if(result.getBody()) {
            showConfirm("Záznam bol úspešne odstránený!");

            tableView.getItems().remove(selectedEmployee);

            setNewRangeOfDisplayedData();
        } else {
            showError("Záznam sa nepodarilo odstrániť!");
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
