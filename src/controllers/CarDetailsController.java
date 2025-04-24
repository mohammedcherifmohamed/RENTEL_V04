package controllers;
import database.DatabaseConnection;
import java.io.File;
import java.net.URI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javax.naming.spi.DirStateFactory;

public class CarDetailsController {
    @FXML private Button remove_carBtn;
    @FXML private TextField brand;
    @FXML private Button btnAdd;
    @FXML private TextField category;
    @FXML private ImageView choosen_image;
    @FXML private Button img_btn;
    @FXML private TextField model;
    @FXML private TextField price;
    @FXML private TextField reg_nbr;

    public String getImagePathFromImageView() {
        Image image = choosen_image.getImage();
        if (image != null && image.getUrl() != null) {
            return new File(URI.create(image.getUrl())).getAbsolutePath();
        }
        return null;
    }
    private VehiclesController vehiclesController;
    public void setVehiclesController(VehiclesController controller) {
        this.vehiclesController = controller;
    }

@FXML
void handleEditButton(ActionEvent event) {
    String car_reg = reg_nbr.getText(); 

    String query = "UPDATE Vehicles SET registration_number = ?, brand = ?, model = ?, category = ?, imagepath = ?, price = ? WHERE registration_number = ?";
    try (Connection conn = DatabaseConnection.connect();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
            
        pstmt.setString(1, reg_nbr.getText());
        pstmt.setString(2, brand.getText());
        pstmt.setString(3, model.getText());
        pstmt.setString(4, category.getText());
        String imagePath = getImagePathFromImageView();
        
        pstmt.setString(5, imagePath);
        pstmt.setDouble(6, Double.parseDouble(price.getText()));
        pstmt.setString(7, car_reg);

        pstmt.executeUpdate();
        
        if (vehiclesController != null) 
            vehiclesController.loadVehicles();
        

        ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    @FXML
    void pick_image(ActionEvent event) {
         String selectedImagePath; 
         FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImagePath = file.getAbsolutePath();
            Image image = new Image(file.toURI().toString());
            choosen_image.setImage(image);
            System.out.println("selectedImagePath"+selectedImagePath);
        }
    }


    public void setCarDetails(String carmodel, String carbrand , String carcategory, double carprice , String rgnbr, String imgpath) {
        brand.setText(carbrand);
        category.setText(carcategory);
        File file = new File(imgpath);
        Image image = new Image(file.toURI().toString());
        choosen_image.setImage(image);
        model.setText(carmodel);
        price.setText(String.valueOf(carprice));
        reg_nbr.setText(rgnbr);
    }


    @FXML
    void remove_car(ActionEvent event) {

        System.out.println("remove car button clicked" + " " + reg_nbr.getText());
        String query = "DELETE FROM Vehicles WHERE registration_number = ?";
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, reg_nbr.getText());
            pstmt.executeUpdate();

            if (vehiclesController != null) {
                vehiclesController.loadVehicles();
            }

            ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}