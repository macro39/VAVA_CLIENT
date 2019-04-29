package src.controller.employee;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import src.model.Customer;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Kamil
 */
public class EmployeeCustomerDetailController implements Initializable {

    @FXML private AnchorPane rootPane;

    @FXML private TableView<Customer> tableView;
    @FXML private TableColumn<Customer, String> columnID;
    @FXML private TableColumn<Customer, String> columnFirstName;
    @FXML private TableColumn<Customer, String> columnLastName;
    @FXML private TableColumn<Customer, String> columnAddress;
    @FXML private TableColumn<Customer, String> columnBankAccount;
    @FXML private TableColumn<Customer, String> columnPhone;

    private Customer customer;
    private Boolean dataChanged = false;

     private ResourceBundle actualLanguage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        actualLanguage = resources;

        columnID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        columnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        columnAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        columnBankAccount.setCellValueFactory(new PropertyValueFactory<>("bankAccount"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));


        tableView.setEditable(true);
        columnFirstName.setCellFactory(TextFieldTableCell.forTableColumn());
        columnLastName.setCellFactory(TextFieldTableCell.forTableColumn());
        columnAddress.setCellFactory(TextFieldTableCell.forTableColumn());
        columnBankAccount.setCellFactory(TextFieldTableCell.forTableColumn());
        columnPhone.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    public void setNaturalPerson(Customer customer) {
        this.customer = customer;
        tableView.setItems(FXCollections.observableArrayList(customer));
    }

    public Boolean getDataChanged() {
        return dataChanged;
    }

    public void changeFirstNameCell(TableColumn.CellEditEvent<Customer, String> naturalPersonStringCellEditEvent) {
        dataChanged = true;
        customer.setFirstName(naturalPersonStringCellEditEvent.getNewValue());
    }

    public void changeLastNameCell(TableColumn.CellEditEvent<Customer, String> naturalPersonStringCellEditEvent) {
        dataChanged = true;
        customer.setLastName(naturalPersonStringCellEditEvent.getNewValue());
    }

    public void changeAddressCell(TableColumn.CellEditEvent<Customer, String> naturalPersonStringCellEditEvent) {
        dataChanged = true;
        customer.setAddress(naturalPersonStringCellEditEvent.getNewValue());
    }

    public void changeBankAccountCell(TableColumn.CellEditEvent<Customer, String> naturalPersonStringCellEditEvent) {
        dataChanged = true;
        customer.setBankAccount(naturalPersonStringCellEditEvent.getNewValue());
    }

    public void changePhoneCell(TableColumn.CellEditEvent<Customer, String> naturalPersonStringCellEditEvent) {
        dataChanged = true;
        customer.setPhone(naturalPersonStringCellEditEvent.getNewValue());
    }
}
