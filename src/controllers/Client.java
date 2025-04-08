package controllers;
import javafx.beans.property.SimpleStringProperty;

public class Client {
    private final SimpleStringProperty name;
    private final SimpleStringProperty contact;
    private final SimpleStringProperty brand;
    private final SimpleStringProperty model;
    private final SimpleStringProperty startDate;
    private final SimpleStringProperty endDate;
    private final SimpleStringProperty totalPrice;

    public Client(String name, String contact, String brand, String model, String startDate, String endDate, String totalPrice) {
        this.name = new SimpleStringProperty(name);
        this.contact = new SimpleStringProperty(contact);
        this.brand = new SimpleStringProperty(brand);
        this.model = new SimpleStringProperty(model);
        this.startDate = new SimpleStringProperty(startDate);
        this.endDate = new SimpleStringProperty(endDate);
        this.totalPrice = new SimpleStringProperty(totalPrice);
    }
    public Client(String name, String contact) {
        this.name = new SimpleStringProperty(name);
        this.contact = new SimpleStringProperty(contact);
        this.brand = new SimpleStringProperty("xxx");
        this.model = new SimpleStringProperty("xxx");
        this.startDate = new SimpleStringProperty("xxx");
        this.endDate = new SimpleStringProperty("xxx");
        this.totalPrice = new SimpleStringProperty("xxx");
    }

    public String getName() { return name.get(); }
    public String getContact() { return contact.get(); }
    public String getBrand() { return brand.get(); }
    public String getModel() { return model.get(); }
    public String getStartDate() { return startDate.get(); }
    public String getEndDate() { return endDate.get(); }
    public String getTotalPrice() { return totalPrice.get(); }
}
