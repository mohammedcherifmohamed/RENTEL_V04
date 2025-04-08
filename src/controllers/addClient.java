package controllers;

import database.DatabaseConnection;
import java.util.ResourceBundle;
import java.net.URL;
import java.sql.ResultSet;
import java.time.temporal.ChronoUnit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.text.TableView;

public class addClient implements Initializable {

    @FXML
    private DatePicker EndDate;

    @FXML
    private DatePicker StartDate;

    @FXML
    private ComboBox<String> brand;

    @FXML
    private Button btnConfirmRental;

    @FXML
    private TextArea contact;

    @FXML
    private ComboBox<String> model;

    @FXML
    private TextField name;

    @FXML
    private TextArea txtRentalHistory;

    @FXML
    private TextField txtTotalPrice;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("from initialize");
        String sql = "SELECT DISTINCT brand FROM Vehicles";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                brand.getItems().add(rs.getString("brand"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void filter_modeles(ActionEvent event) {
        System.out.println(brand.getSelectionModel().getSelectedItem());
        String selected_brand = brand.getSelectionModel().getSelectedItem();
        String sql = "SELECT model FROM Vehicles WHERE brand = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, selected_brand);
            ResultSet rs = stmt.executeQuery();

            model.getItems().clear(); 

            while (rs.next()) {
                model.getItems().add(rs.getString("model"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private ClientController clientController;

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }
    
    @FXML
    void ConfirmRental(ActionEvent event) {
        if (StartDate.getValue() != null && EndDate.getValue() != null) {
            long daysBetween = ChronoUnit.DAYS.between(StartDate.getValue(), EndDate.getValue());
    
            String checkSql = "SELECT COUNT(*) FROM Clients WHERE name = ? AND contact_details = ?";
            String insertSql = "INSERT INTO Clients (name, contact_details) VALUES (?, ?)";
    
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
    
                checkStmt.setString(1, name.getText());
                checkStmt.setString(2, contact.getText());
                ResultSet rs = checkStmt.executeQuery();
    
                if (rs.next() && rs.getInt(1) == 0) { // If client does not exist
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, name.getText());
                        insertStmt.setString(2, contact.getText());
                        insertStmt.executeUpdate();
                        System.out.println("New client added.");
    
                        // Refresh the table
                        if (clientController != null) {
                            clientController.loadSampleData();
                        }
    
                        // Close the window
                        ((Stage) btnConfirmRental.getScene().getWindow()).close();
                    }
                } else {
                    System.out.println("Client already exists.");
                }
    
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Please select both start and end dates.");
        }
    }
    

    

    @FXML
    void get_thisCar_info(ActionEvent event) {
        String selected_brand = brand.getSelectionModel().getSelectedItem();
        String selected_model = model.getSelectionModel().getSelectedItem();

        if (selected_brand == null || selected_model == null || StartDate.getValue() == null || EndDate.getValue() == null) {
            System.out.println("Please select brand, model, and rental dates.");
            return;
        }

        String sql = "SELECT price FROM Vehicles WHERE brand = ? AND model = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, selected_brand);
            stmt.setString(2, selected_model);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double price_per_day = rs.getDouble("price");
                long daysBetween = ChronoUnit.DAYS.between(StartDate.getValue(), EndDate.getValue());
                double total_price = price_per_day * daysBetween;
                txtTotalPrice.clear(); 
                txtTotalPrice.setText(String.valueOf(total_price));
            } else {
                System.out.println("No price found for the selected vehicle.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
