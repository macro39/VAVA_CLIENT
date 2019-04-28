package src.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Contract {

    @JsonProperty("contractId")
    private Integer contractID;

    @JsonProperty("car")
    private Car car;

    @JsonProperty("customer")
    private Customer customer;

    @JsonProperty("employee")
    private Employee employee;

    @JsonProperty("dateFrom")
    private Date dateFrom;

    @JsonProperty("dateTo")
    private Date dateTo;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("dateOfCreating")
    private Date dateOfCreating;

    public Contract() {
    }

    public Contract(Car car, Customer customer,
                    Employee employee, Date dateFrom, Date dateTo,
                    Double price, Date dateOfCreating) {
        this.car = car;
        this.customer = customer;
        this.employee = employee;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.price = price;
        this.dateOfCreating = dateOfCreating;
    }

    public Integer getContractID() {
        return contractID;
    }

    public void setContractID(Integer contractID) {
        this.contractID = contractID;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getDateOfCreating() {
        return dateOfCreating;
    }

    public void setDateOfCreating(Date dateOfCreating) {
        this.dateOfCreating = dateOfCreating;
    }
}
