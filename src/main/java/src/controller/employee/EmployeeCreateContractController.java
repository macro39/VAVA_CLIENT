package src.controller.employee;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import src.model.Car;
import src.model.Contract;
import src.model.Customer;
import src.model.Employee;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Jozef
 */
public class EmployeeCreateContractController extends EmployeeBackToMenu implements Initializable {

    @FXML private AnchorPane rootPane;

    @FXML private JFXTextField textFieldID;
    @FXML private JFXTextField textFieldVIN;

    @FXML private JFXDatePicker datePickerFrom;
    @FXML private JFXDatePicker datePickerTo;

    @FXML private JFXProgressBar progressBar;

    // logged employee
    private Employee employee;

    // [optional] - car from searching
    private Car car = null;

    // [optional\ - person from searching
    private Customer customer = null;

    private ResourceBundle actualLanguage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        actualLanguage = resources;

        textFieldID.setEditable(false);
        textFieldVIN.setEditable(false);

        progressBar.setVisible(false);
    }

    // SETTERS AND GETTERS FOR ENTITY

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setCar(Car car) {
        this.car = car;
        setCarVin();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        setCustomerID();
    }

    public void setCarVin() {
        textFieldVIN.setText(car.getCarVIN());
    }

    public void setCustomerID() {
        textFieldID.setText(customer.getCustomerID());
    }

    public void setSelectedCarVIN(String VIN) {
        textFieldVIN.setText(VIN);
    }

    public void setSelectedCustomerID(String ID) {
        textFieldID.setText(ID);
    }

    // SETTERS AND GETTERS FOR LABELS

    public String getTextFieldID() {
        return textFieldID.getText();
    }

    public String getTextFieldVin() {
        return textFieldVIN.getText();
    }

    public Date getDateFrom() {
        if (datePickerFrom.getValue()==null){
            return null;
        }
        Date date = Date.valueOf(datePickerFrom.getValue());

        return date;
    }

    public Date getDateTo() {
        if (datePickerTo.getValue()==null){
            return null;
        }
        Date date = Date.valueOf(datePickerTo.getValue());

        return date;
    }

    /**
     * Switched to the scene where employee can find Customer and create agreement with him.
     * @param actionEvent
     */
    public void buttonSearchCustomerPushed(ActionEvent actionEvent) {
        Parent parent = null;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/employee/employee_search_customer.fxml"),actualLanguage);
            parent = (Parent) loader.load();

            EmployeeSearchCustomerController employeeSearchCustomerController = loader.getController();
            employeeSearchCustomerController.setEmployee(employee);
            employeeSearchCustomerController.setOpenedFromContractScene(true);

            if(car != null) {
                employeeSearchCustomerController.setCar(car);
            } else if (!getTextFieldVin().trim().isEmpty()) {
                employeeSearchCustomerController.setCarVIN(getTextFieldVin());
            }

            employeeSearchCustomerController.addItemsToList();
            employeeSearchCustomerController.addItemsToTable();

            employeeSearchCustomerController.setNewRangeOfDisplayedData();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene newScene = new Scene(parent);

        //This line gets the Stage information
        Stage currentStage = (Stage) rootPane.getScene().getWindow();

        currentStage.setScene(newScene);
        currentStage.show();
    }

    /**
     * Switched to the scene where employee can find car and create agreement with it.
     * @param actionEvent
     */
    public void buttonSearchCarPushed(ActionEvent actionEvent) {
        Parent parent = null;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/employee/employee_search_car.fxml"),actualLanguage);
            parent = (Parent) loader.load();

            EmployeeSearchCarController employeeSearchCarController = loader.getController();
            employeeSearchCarController.setEmployee(employee);
            employeeSearchCarController.setOpenedFromContractScene(true);

            if(customer != null) {
                employeeSearchCarController.setCustomer(customer);
            } else if (!getTextFieldID().trim().isEmpty()) {
                employeeSearchCarController.setCustomerID(getTextFieldID());
            }

            employeeSearchCarController.addItemsToList();
            employeeSearchCarController.addItemsToTable();

            employeeSearchCarController.setNewRangeOfDisplayedData();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene newScene = new Scene(parent);

        //This line gets the Stage information
        Stage currentStage = (Stage) rootPane.getScene().getWindow();

        currentStage.setScene(newScene);
        currentStage.show();
    }

    /**
     * Control if date to is bigger than date from. If there is all correct info new scene will be shown.
     * @param actionEvent
     */
    public void btnCreateContractPushed(ActionEvent actionEvent) {
        if (getDateTo() == null || getDateFrom() == null || getDateTo().before(getDateFrom())) {
            showWarning(actualLanguage.getString("notificationBadRentalPeriod"));
        } else if(car == null) {
            showError(actualLanguage.getString("notificationNoEnterData"));
        } else if(customer == null) {
            showError(actualLanguage.getString("notificationNoEnterData"));
        }
        else {
            long diff = getDateTo().getTime() - getDateFrom().getTime();

            double price = car.getPricePerDay()*(int)(TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS));

            Date date = Date.valueOf(
                    new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));

            Contract contract = new Contract(car,customer,employee,getDateFrom(),getDateTo(),price,date);

            Parent parent;
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/view/employee/employee_contract_detail.fxml"),
                        actualLanguage);
                parent = (Parent) loader.load();

                EmployeeContractDetailController employeeContractDetailController = loader.getController();
                employeeContractDetailController.setEmployee(employee);
                employeeContractDetailController.setContract(contract);

                employeeContractDetailController.setDateFROM(getDateFrom());
                employeeContractDetailController.setDateTO(getDateTo());

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

                    Scene newScene = new Scene(parent);

                    //This line gets the Stage information
                    Stage currentStage = (Stage) rootPane.getScene().getWindow();

                    currentStage.setScene(newScene);
                    currentStage.show();
                });

                Thread thread = new Thread(setLabels);
                thread.setDaemon(true);
                thread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Calls another method called backToMenu().
     * @param actionEvent
     */
    public void btnBackPushed(ActionEvent actionEvent) {
        backToMenu(rootPane,employee,actualLanguage);
    }
}
