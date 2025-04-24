package controllers;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;    

import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent; 
import javafx.scene.input.MouseEvent;
import database.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VehiclesController implements Initializable {
    @FXML private javafx.scene.control.ComboBox<String> searchTypeComboBox;
    @FXML private GridPane vehicleGrid;
    @FXML    private Button AddVehicle;

    @FXML
    private TextField VehicleSearch;
    
    @FXML
    private TextField SearchBar;

    private Connection connection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchTypeComboBox.setItems(javafx.collections.FXCollections.observableArrayList("Brand", "Model", "Category"));
        searchTypeComboBox.getSelectionModel().select("Category"); 
        connection = DatabaseConnection.connect();
        if (connection != null) {
            loadVehicles();
        } else {
            System.out.println("Database connection failed.");
        }
    }

    @FXML
    public void handleAddVehicle() {
        // System.out.println("Add Vehicle Button Clicked!");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/addCar.fxml"));
            Parent root = loader.load();
    
            Stage stage = new Stage();
            stage.setTitle("Add Vehicle");
            stage.setScene(new Scene(root));
    
            AddCar controller = loader.getController();
            controller.setStage(stage);
    
            stage.showAndWait();
    
            Vehicle newVehicle = controller.getVehicle();
            if (newVehicle != null) {
                DatabaseConnection.AddCar(newVehicle.getreg_nbr(), newVehicle.getBrand(), newVehicle.getModel(), newVehicle.getCategory(), newVehicle.getPrice(), "Available",newVehicle.getPath());
                loadVehicles();
                stage.close();
                System.out.println("Vehicle Added: " + newVehicle.getBrand() +
                    " " + newVehicle.getModel() + ", Price: $" + newVehicle.getPrice()+" "+newVehicle.getPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   public void loadVehicles() {
    vehicleGrid.getChildren().clear(); // ✅ Clear old vehicle cards

    String query = "SELECT * FROM vehicles"; 
    try (PreparedStatement stmt = connection.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        int column = 0, row = 0;
        while (rs.next()) {
            String name = rs.getString("brand");
            String category = rs.getString("model");
            String price = rs.getString("price");
            String imagePath = rs.getString("imagepath");
            String regNbr = rs.getString("registration_number");
            String availability = rs.getString("availability_status"); // NEW

            BorderPane vehicleCard = createVehicleCard(name, category, price, imagePath, regNbr, availability); // UPDATED

            vehicleGrid.add(vehicleCard, column, row);  

            column++;
            if (column == 4) { // Adjust based on layout preference
                column = 0;
                row++;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private BorderPane createVehicleCard(String name, String category, String price, String imagePath, String reg_nbr, String availability) {
    BorderPane card = new BorderPane();
    card.setOnMousePressed(this::seeDetails);
    card.setStyle("-fx-background-color: #2C2F4A; -fx-background-radius: 20;");
    card.setPrefSize(200, 200);
    card.setId(reg_nbr);

    ImageView carImage = new ImageView();
    carImage.setFitHeight(110);
    carImage.setFitWidth(150);
    carImage.setPreserveRatio(true);
    try {
        carImage.setImage(new Image("file:" + imagePath)); // Ensure correct path format
    } catch (Exception e) {
        System.out.println("Image not found: " + imagePath);
    }

    StackPane imageWrapper = new StackPane(carImage);

    if (!availability.equals("Available")) {
        Label rentedLabel = new Label("RENTED");
        rentedLabel.setStyle(
            "-fx-background-color: #e74c3c;" + // red
            "-fx-text-fill: white;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 3 6 3 6;"
        );
        StackPane.setAlignment(rentedLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(rentedLabel, new Insets(5, 5, 0, 0));
        imageWrapper.getChildren().add(rentedLabel);
    }

    VBox centerBox = new VBox(10);
    centerBox.setStyle("-fx-alignment: center;");
    Label nameLabel = new Label(name);
    nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 17px;");

    Label categoryLabel = new Label(category);
    categoryLabel.setStyle("-fx-text-fill: #909090;");

    centerBox.getChildren().addAll(nameLabel, categoryLabel);

    HBox bottomBox = new HBox(5);
    bottomBox.setStyle("-fx-alignment: center;");

    Label priceLabel = new Label(price);
    priceLabel.setStyle("-fx-text-fill: #4a61ff; -fx-font-size: 20px;");

    Label perDayLabel = new Label("/DAY");
    perDayLabel.setStyle("-fx-text-fill: white; -fx-font-size: 9px;");

    bottomBox.getChildren().addAll(priceLabel, perDayLabel);

    card.setCenter(centerBox);
    card.setBottom(bottomBox);
    card.setTop(imageWrapper); // Use image with conditional badge
    BorderPane.setMargin(imageWrapper, new Insets(10));
    BorderPane.setMargin(centerBox, new Insets(10, 0, 0, 0));
    BorderPane.setMargin(bottomBox, new Insets(0, 0, 10, 5));

    return card;
}


    

    @FXML
    void seeDetails(MouseEvent event) {
        String query = "SELECT * FROM vehicles"; 

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
                    BorderPane clickedCard = (BorderPane) event.getSource();
                    // VBox topbox = (VBox) clickedCard.getCenter();
                    // VBox centerBox = (VBox) clickedCard.getCenter();
                    // Label nameLabel = (Label) centerBox.getChildren().get(0);
                    // Label categoryLabel = (Label) centerBox.getChildren().get(1);
                    System.out.println("REGISTRATION NUMBER : "+ clickedCard.getId());

                    // String carName = nameLabel.getText();
                    // String carCategory = categoryLabel.getText();

                    // System.out.println("Clicked Car: " + carName + " - " + carCategory);

                    while (rs.next()) {
                        String regNbr = rs.getString("registration_number");
                        
                        if (regNbr.equals(clickedCard.getId())) { 
                            String brand = rs.getString("brand");
                            String model = rs.getString("model");
                            String category = rs.getString("category");
                            double price = rs.getDouble("price");
                            // createVehicleCard(model, brand, category, price, imagePath);
                            String imagePath = rs.getString("imagepath");
                
                            // System.out.println("FROM seeDetails :" + brand + "_"+model+ "_"+category+ "_"+price+ "_"+regNbr+ "_"+imagePath);
                            showCarDetails( model,  brand,  category,  price,regNbr,  imagePath);
                        // BorderPane vehicleCard = createVehicleCard(name, category, price, imagePath, regNbr);
                        break; 
                    }
                }
                
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
}
    
private void showCarDetails(String model, String brand, String category, double price, String rgnbr, String imgpath) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CarDetails.fxml"));
        Parent root = loader.load();

        CarDetailsController controller = loader.getController();
        controller.setCarDetails(model, brand, category, price, rgnbr, imgpath);
        controller.setVehiclesController(this); 

        Stage stage = new Stage();
        stage.setTitle("Car Details");
        stage.setScene(new Scene(root));
        stage.show();
    } catch (IOException e) {
        
        // e.printStackTrace();
    }
}


        @FXML
        void search(KeyEvent event) {
            String searchValue = SearchBar.getText().toLowerCase();
            String selectedKey = searchTypeComboBox.getValue();
            if (selectedKey == null) selectedKey = "Category"; // fallback
            // String selectedKey = "Category";
        
            String query = "SELECT * FROM vehicles";
        
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
        
                int column = 0, row = 0;
                vehicleGrid.getChildren().clear();
        
                while (rs.next()) {
                    String brand = rs.getString("brand");
                    String model = rs.getString("model");
                    String category = rs.getString("category");
                    String price = rs.getString("price");
                    String imagePath = rs.getString("imagepath");
                    String regNbr = rs.getString("registration_number");
                    String availability = rs.getString("availability_status");
        
                    boolean matches = switch (selectedKey) {
                        case "Brand" -> brand.toLowerCase().contains(searchValue);
                        case "Model" -> model.toLowerCase().contains(searchValue);
                        case "Category" -> category.toLowerCase().contains(searchValue);
                        default -> false;
                    };
        
                    if (matches) {
                        BorderPane vehicleCard = createVehicleCard(brand, model, price, imagePath, regNbr, availability);
                        vehicleGrid.add(vehicleCard, column, row);
                        column++;
                        if (column == 4) {
                            column = 0;
                            row++;
                        }
                    }
                }
        
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        

}
