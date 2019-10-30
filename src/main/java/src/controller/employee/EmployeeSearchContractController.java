package src.controller.employee;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import src.model.Contract;
import src.model.Employee;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jozef
 */
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

    private static Logger LOG = Logger.getLogger(EmployeeSearchContractController.class.getName());

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

    /**
     * Set up current date into labels.
     */
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

    /**
     * Connect to database for add items to list. Later it will be shown in the tableView.
     */
    public void addItemsToList() {
        String resourceURL = "http://localhost:8080/api/contract/byOffSet/" + offSet;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<List<Contract>> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Contract>> result;

        try {
            result =  restTemplate.exchange(resourceURL,
                    HttpMethod.GET, entity, new ParameterizedTypeReference<List<Contract>>() {
                    });
        } catch (Exception e) {
            showError(actualLanguage.getString("notificationNoResponseServer"));
            return;
        }

        observableList = FXCollections.observableArrayList();

        observableList.addAll(result.getBody());
    }

    /**
     * Connect to database for add items to list. Later it will be shown in the tableView.
     */
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

        ResponseEntity<List<Contract>> result;

        try {
            result = restTemplate.exchange(resourceURL,
                    HttpMethod.GET, entity, new ParameterizedTypeReference<List<Contract>>() {
                    });
        } catch (Exception e) {
            showError(actualLanguage.getString("notificationNoResponseServer"));
            return;
        }

        observableList = FXCollections.observableArrayList();

        observableList.addAll(result.getBody());
    }

    public void addItemsToListCreatedByEmployee() {
        String resourceURL = "http://localhost:8080/api/contract/byEmployee/" + employee.getEmployeeID();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<Employee> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Contract>> result;

        try {
            result = restTemplate.exchange(resourceURL,
                    HttpMethod.GET, entity, new ParameterizedTypeReference<List<Contract>>() {
                    });
        } catch (Exception e) {
            showError(actualLanguage.getString("notificationNoResponseServer"));
            return;
        }

        observableList = FXCollections.observableArrayList();

        observableList.addAll(result.getBody());
    }

    /**
     * Fill tableView with items.
     */
    public void addItemsToTable() {
        tableView.setItems(observableList);

        filteredList = new FilteredList(observableList,e->true);

        if(observableList.size() == 500) {
            buttonNextData.setDisable(false);
        }
    }

    /**
     * Switched to new scene for detail of contract.
     * @param actionEvent
     */
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

    /**
     * Connect to server and contract record will be deleted from database.
     * @param actionEvent
     */
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
            LOG.log(Level.WARNING, actualLanguage.getString("notificationNoResponseServer"));
            showError(actualLanguage.getString("notificationNoResponseServer"));
            return;
        }

        showConfirm(actualLanguage.getString("notificationDeleteNotSuccessfulRecord"));

        observableList.remove(selectedContract);
        tableView.refresh();
    }

    /**
     * Method creates pdf file, and export a contract into pdf file.
     * @param actionEvent
     */
    public void exportMenuSelected(ActionEvent actionEvent) {
        Contract contract = tableView.getSelectionModel().getSelectedItem();

        if(contract == null) {
            return;
        }

        Document document = new Document();
        String fileName = "contract" + contract.getContractID() + ".pdf";
        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Paragraph spaces = new Paragraph("\n");

        Paragraph carInfo = new Paragraph(actualLanguage.getString("borrowedVehicleAgreementLabel"),
                FontFactory.getFont(FontFactory.TIMES, 18, Font.BOLD|Font.UNDERLINE));
        carInfo.setAlignment(Element.ALIGN_CENTER);

        Paragraph vehicleInfoTitle = new Paragraph(actualLanguage.getString("vehicleInformationLabel"),
                FontFactory.getFont(FontFactory.TIMES, 14, Font.BOLD|Font.UNDERLINE)
        );

        Paragraph vehicleInfo = new Paragraph(
                actualLanguage.getString("vinLabel") + " " + contract.getCarVIN() + "\n"
                + actualLanguage.getString("licencePlateLabel") + " " + contract.getCar().getCarSPZ() + "\n"
                + actualLanguage.getString("brandLabel") + " " + contract.getCar().getBrand() + "\n"
                + actualLanguage.getString("modelLabel") + " " + contract.getCar().getModel() + "\n"
                + actualLanguage.getString("yearOfProductionLabel") + " " +
                        contract.getCar().getYearOfProduction() + "\n",
                FontFactory.getFont(FontFactory.TIMES, 12)
        );

        Paragraph borrowerInfoTitle = new Paragraph(actualLanguage.getString("borrowerInformationLabel"),
                FontFactory.getFont(FontFactory.TIMES, 14, Font.BOLD|Font.UNDERLINE)
        );

        Paragraph borrowerInfo = new Paragraph(
                actualLanguage.getString("firstNameLabel") + " " +
                        contract.getCustomer().getFirstName() + "\n"
                + actualLanguage.getString("lastNameLabel") + " " + contract.getCustomer().getLastName() + "\n"
                + actualLanguage.getString("idLabel") + " " + contract.getCustomer().getCustomerID() + "\n"
                + actualLanguage.getString("adressLabel") + " " + contract.getCustomer().getAddress() + "\n"
                + actualLanguage.getString("bankAccountLabel") + " " + contract.getCustomer().getAddress() + "\n"
                + actualLanguage.getString("phoneLabel") + " " + contract.getCustomer().getPhone() + "\n",
                FontFactory.getFont(FontFactory.TIMES, 12)
        );

        Paragraph rentalPeriodTitle = new Paragraph(
                actualLanguage.getString("rentalPeriodLabel"),
                FontFactory.getFont(FontFactory.TIMES, 16, Font.BOLD|Font.UNDERLINE)
        );

        Paragraph rentalPeriod = new Paragraph(
                actualLanguage.getString("loanDateLabel") + " " + contract.getDateFrom() + " - "
                + actualLanguage.getString("toBeReturnDate") + " " + contract.getDateTo() + "\n\n"
                + actualLanguage.getString("totalPriceLabel") + " " + contract.getPrice() + "€",
                FontFactory.getFont(FontFactory.TIMES, 12)
        );

        Paragraph dealerInfoTitle = new Paragraph(
                actualLanguage.getString("dealerLabel"),
                FontFactory.getFont(FontFactory.TIMES, 16, Font.BOLD|Font.UNDERLINE)
        );

        Paragraph dealerInfo = new Paragraph(
                contract.getEmployee().getFirstName() + " " + contract.getEmployee().getLastName() + "\n",
                FontFactory.getFont(FontFactory.TIMES, 12)
        );

        Paragraph dateOfCreatingTitle = new Paragraph(
                actualLanguage.getString("dots") +
                actualLanguage.getString("dayLabel") + contract.getDateOfCreating(),
                FontFactory.getFont(FontFactory.TIMES, 16)
        );

        Paragraph signature1 = new Paragraph(
                actualLanguage.getString("dots") + "\n"  +
                actualLanguage.getString("borrowerSignatureLabel")
        );

        Paragraph signature2 = new Paragraph(
                actualLanguage.getString("dots") + "\n"  +
                        actualLanguage.getString("dealerSignatureLabel")
        );

        document.open();
        try {
            document.add(carInfo);
            document.add(spaces);
            document.add(vehicleInfoTitle);
            document.add(vehicleInfo);
            document.add(spaces);
            document.add(borrowerInfoTitle);
            document.add(borrowerInfo);
            document.add(spaces);
            document.add(rentalPeriodTitle);
            document.add(rentalPeriod);
            document.add(spaces);
            document.add(dealerInfoTitle);
            document.add(dealerInfo);
            document.add(spaces);
            document.add(dateOfCreatingTitle);
            document.add(spaces);
            document.add(spaces);
            document.add(signature1);
            document.add(spaces);
            document.add(signature2);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        document.close();

        String path = null;
        try {
            path = new File(fileName).getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ProcessBuilder pb = new ProcessBuilder("explorer.exe", "/select," + path);
        pb.redirectError();
        try {
            Process proc = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param keyEvent
     */
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

    /**
     * If there is more than 500 records, method will load next 500.
     * @param actionEvent
     */
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

    /**
     * Method will load previous 500 records.
     * @param actionEvent
     */
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

    /**
     * Search in database based on some criteria.
     * @param actionEvent
     */
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

                showInformation(actualLanguage.getString("notificationFoundItemsAreMoreThan")
                        + observableList.size() + ".");

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

    /**
     * Data without any criteria are searching and displayed.
     * @param actionEvent
     */
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

    /**
     * Method will show contract which belongs to logged employee.
     * @param actionEvent
     */
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

                showInformation(actualLanguage.getString("notificationFoundItemsAreMoreThan")
                        + observableList.size() + ".");

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

    /**
     * Switched to new scene back to menu.
     */
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

}
