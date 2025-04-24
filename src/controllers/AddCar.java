package controllers;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.control.Label;


public class AddCar {
    @FXML private TextField brand;
    @FXML private Button btnAdd;
    @FXML private TextField category;
    @FXML private Label dragLabel;
    @FXML private TextField model;
    @FXML private TextField price;
    @FXML private TextField reg_nbr;
    @FXML private Button img_btn;
    @FXML private ImageView choosen_image;
    private Stage stage;

    private Vehicle newVehicle;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Vehicle getVehicle() {
        return newVehicle;
    }

    private String selectedImagePath; 

    @FXML
    public void pick_image(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImagePath = file.getAbsolutePath();
            Image image = new Image(file.toURI().toString());
            choosen_image.setImage(image);
            System.out.println(selectedImagePath);
        }
    }


    @FXML
    public void handleAddButton() {
        newVehicle = new Vehicle(
            brand.getText(),
            model.getText(),
            category.getText().toLowerCase(),
            reg_nbr.getText(),
            selectedImagePath,
            Double.parseDouble(price.getText())
        );

        stage.close();
    }
}
