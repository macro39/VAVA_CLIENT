package src.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Kamil
 */
public class CarInfo {
    @JsonProperty("carInfoID")
    private Integer carInfoID;

    @JsonProperty("brand")
    private String brand = "Audi";

    @JsonProperty("model")
    private String model = "Q7";

    @JsonProperty("bodyStyle")
    private String bodyStyle = "Sedan";

    @JsonProperty("engineCapacity")
    private Double engineCapacity = 2.0;

    @JsonProperty("enginePower")
    private Integer enginePower = 120;

    @JsonProperty("gearBox")
    private String gearBox = "manuálna";

    @JsonProperty("fuel")
    private String fuel = "diesel";

    @JsonProperty("color")
    private String color = "biela";

    @JsonProperty("pricePerDay")
    private Double pricePerDay = 50.0;

    public CarInfo() {
    }

    public CarInfo(String brand, String model, String bodyStyle,
                   Double engineCapacity, Integer enginePower, String gearBox,
                   String fuel, String color, Double pricePerDay) {
        this.brand = brand;
        this.model = model;
        this.bodyStyle = bodyStyle;
        this.engineCapacity = engineCapacity;
        this.enginePower = enginePower;
        this.gearBox = gearBox;
        this.fuel = fuel;
        this.color = color;
        this.pricePerDay = pricePerDay;
    }

    public CarInfo(Integer carInfoID, String brand, String model, String bodyStyle, Double engineCapacity,
                   Integer enginePower, String gearBox, String fuel, String color, Double pricePerDay) {
        this.carInfoID = carInfoID;
        this.brand = brand;
        this.model = model;
        this.bodyStyle = bodyStyle;
        this.engineCapacity = engineCapacity;
        this.enginePower = enginePower;
        this.gearBox = gearBox;
        this.fuel = fuel;
        this.color = color;
        this.pricePerDay = pricePerDay;
    }

    public Integer getCarInfoID() {
        return carInfoID;
    }

    public void setCarInfoID(Integer carInfoID) {
        this.carInfoID = carInfoID;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBodyStyle() {
        return bodyStyle;
    }

    public void setBodyStyle(String bodyStyle) {
        this.bodyStyle = bodyStyle;
    }

    public Double getEngineCapacity() {
        return engineCapacity;
    }

    public void setEngineCapacity(Double engineCapacity) {
        this.engineCapacity = engineCapacity;
    }

    public Integer getEnginePower() {
        return enginePower;
    }

    public void setEnginePower(Integer enginePower) {
        this.enginePower = enginePower;
    }

    public String getGearBox() {
        return gearBox;
    }

    public void setGearBox(String gearBox) {
        this.gearBox = gearBox;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(Double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }
}
