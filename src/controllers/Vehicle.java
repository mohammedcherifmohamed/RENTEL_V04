package controllers;

public class Vehicle {
    private String brand;
    private String model;
    private String category;
    private String register_nbr;  
    private String imagePath;  
    private double price;

    public Vehicle(String brand, String model, String category, String register_nbr, String imagePath ,double price ) {
        this.brand = brand;     
        this.model = model;
        this.category = category;
        this.price = price;
        this.register_nbr = register_nbr;
        this.imagePath= imagePath;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getCategory() {
        return category;
    }
    public String getreg_nbr() {
        return  register_nbr;
    }
    public String getPath() {
        return  imagePath;
    }

    public double getPrice() {
        return price;
    }
}
