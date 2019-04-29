package src.controller.admin;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import src.model.Employee;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Kamil
 */
public class AdminEmployeeDetailController implements Initializable {

    @FXML private AnchorPane rootPane;

    @FXML private TableView<Employee> tableView;

    @FXML private TableColumn<Employee, Integer> columnID;
    @FXML private TableColumn<Employee, String> columnFirstName;
    @FXML private TableColumn<Employee, String> columnLastName;
    @FXML private TableColumn<Employee, String> columnLogin;
    @FXML private TableColumn<Employee, String> columnPassword;
    @FXML private TableColumn<Employee, String> columnPhone;
    @FXML private TableColumn<Employee, String> columnType;

    private Employee employee;

    private Boolean dataChanged = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnID.setCellValueFactory(new PropertyValueFactory<>("employeeID"));
        columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        columnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        columnLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        columnPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        columnType.setCellValueFactory(new PropertyValueFactory<>("type"));

        tableView.setEditable(true);
        columnFirstName.setCellFactory(TextFieldTableCell.forTableColumn());
        columnLastName.setCellFactory(TextFieldTableCell.forTableColumn());
        columnPassword.setCellFactory(TextFieldTableCell.forTableColumn());
        columnPhone.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        tableView.setItems(FXCollections.observableArrayList(employee));
    }

    public Boolean getDataChanged() {
        return dataChanged;
    }

    public void changeFirstNameCell(TableColumn.CellEditEvent<Employee, String> employeeStringCellEditEvent) {
        dataChanged = true;
        employee.setFirstName(employeeStringCellEditEvent.getNewValue());
    }

    public void changeLastNameCell(TableColumn.CellEditEvent<Employee, String> employeeStringCellEditEvent) {
        dataChanged = true;
        employee.setLastName(employeeStringCellEditEvent.getNewValue());
    }

    public void changePasswordCell(TableColumn.CellEditEvent<Employee, String> employeeStringCellEditEvent) {
        dataChanged = true;
        employee.setPassword(employeeStringCellEditEvent.getNewValue());
    }

    public void changePhoneCell(TableColumn.CellEditEvent<Employee, String> employeeStringCellEditEvent) {
        dataChanged = true;
        employee.setPhone(employeeStringCellEditEvent.getNewValue());
    }
}
