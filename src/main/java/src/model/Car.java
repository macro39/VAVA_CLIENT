package src.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class Car {
    @JsonProperty("carVIN")
    private String carVIN;

    @JsonProperty("carInfo")
    private CarInfo carInfo;

    @JsonProperty("yearOfProduction")
    private Date yearOfProduction;

    @JsonProperty("mileAge")
    private Integer mileAge;

    @JsonProperty("carSPZ")
    private String carSPZ;

    @JsonProperty("carRepairs")
    private Set<CarRepair> carRepairs;

    public Car() {
    }

    public Car(String carVIN, CarInfo carInfo, Date yearOfProduction, Integer mileAge, String carSPZ) {
        this.carVIN = carVIN;
        this.carInfo = carInfo;
        this.yearOfProduction = yearOfProduction;
        this.mileAge = mileAge;
        this.carSPZ = carSPZ;
    }

    public String getCarVIN() {
        return carVIN;
    }

    public void setCarVIN(String carVIN) {
        this.carVIN = carVIN;
    }

    public CarInfo getCarInfo() {
        return carInfo;
    }

    public void setCarInfo(CarInfo carInfo) {
        this.carInfo = carInfo;
    }

    public String getYearOfProduction() {
        String newFormat = new SimpleDateFormat("yyyy-MM-dd").format(yearOfProduction);
        return  newFormat;
    }

    public void setYearOfProduction(Date yearOfProduction) {
        this.yearOfProduction = yearOfProduction;
    }

    public Integer getMileAge() {
        return mileAge;
    }

    public void setMileAge(Integer mileAge) {
        this.mileAge = mileAge;
    }

    public String getCarSPZ() {
        return carSPZ;
    }

    public void setCarSPZ(String carSPZ) {
        this.carSPZ = carSPZ;
    }

    public Integer getCarInfoID() {
        return this.carInfo.getCarInfoID();
    }

    public void setCarInfoID(Integer carInfoID) {
        this.carInfo.setCarInfoID(carInfoID);
    }

    public String getBrand() {
        return this.carInfo.getBrand();
    }

    public void setBrand(String brand) {
        this.getCarInfo().setBrand(brand);
    }

    public String getModel() {
        return this.carInfo.getModel();
    }

    public void setModel(String model) {
        this.carInfo.setModel(model);
    }

    public String getBodyStyle() {
        return this.carInfo.getBodyStyle();
    }

    public void setBodyStyle(String bodyStyle) {
        this.carInfo.setBodyStyle(bodyStyle);
    }

    public Double getEngineCapacity() {
        return this.carInfo.getEngineCapacity();
    }

    public void setEngineCapacity(Double engineCapacity) {
        this.carInfo.setEngineCapacity(engineCapacity);
    }

    public Integer getEnginePower() {
        return this.carInfo.getEnginePower();
    }

    public void setEnginePower(Integer enginePower) {
        this.carInfo.setEnginePower(enginePower);
    }

    public String getGearBox() {
        return this.carInfo.getGearBox();
    }

    public void setGearBox(String gearBox) {
        this.carInfo.setGearBox(gearBox);
    }

    public String getFuel() {
        return this.carInfo.getFuel();
    }

    public void setFuel(String fuel) {
        this.carInfo.setFuel(fuel);
    }

    public String getColor() {
        return this.carInfo.getColor();
    }

    public void setColor(String color) {
        this.carInfo.setColor(color);
    }

    public Double getPricePerDay() {
        return this.carInfo.getPricePerDay();
    }

    public void setPricePerDay(Double pricePerDay) {
        this.carInfo.setPricePerDay(pricePerDay);
    }

    public Set<CarRepair> getCarRepairs() {
        return carRepairs;
    }

    public void setCarRepairs(Set<CarRepair> carRepairs) {
        this.carRepairs = carRepairs;
    }

    public void addRepair(CarRepair carRepair) {
        this.carRepairs.add(carRepair);
        return;
    }


}
