package src.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Kamil
 */
public class CarRepair {

    @JsonProperty("repairID")
    private Integer repairID;

    @JsonProperty("typeOfRepair")
    private String typeOfRepair;

    @JsonProperty("dateOfService")
    private Date dateOfService;

    @JsonProperty("price")
    private Float price;

    @JsonProperty("serviceName")
    private String serviceName;

    @JsonProperty("serviceLocation")
    private String serviceLocation;

    public CarRepair() {
    }

    public CarRepair(String typeOfRepair, Date dateOfService, Float price, String serviceName, String serviceLocation) {
        this.typeOfRepair = typeOfRepair;
        this.dateOfService = dateOfService;
        this.price = price;
        this.serviceName = serviceName;
        this.serviceLocation = serviceLocation;
    }

    public Integer getRepairID() {
        return repairID;
    }

    public void setRepairID(Integer repairID) {
        this.repairID = repairID;
    }

    public String getTypeOfRepair() {
        return typeOfRepair;
    }

    public void setTypeOfRepair(String typeOfRepair) {
        this.typeOfRepair = typeOfRepair;
    }

    public String getDateOfService() {
        String newFormat = new SimpleDateFormat("yyyy-MM-dd").format(dateOfService);
        return  newFormat;
    }

    public void setDateOfService(Date dateOfService) {
        this.dateOfService = dateOfService;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceLocation() {
        return serviceLocation;
    }

    public void setServiceLocation(String serviceLocation) {
        this.serviceLocation = serviceLocation;
    }
}
