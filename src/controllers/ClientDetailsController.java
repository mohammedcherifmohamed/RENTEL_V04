package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

import database.DatabaseConnection;

public class ClientDetailsController implements Initializable  {
    @FXML private Button rm_btn;
    @FXML private DatePicker EndDate;
    @FXML private DatePicker StartDate;
    @FXML private ComboBox<String> brand;
    @FXML private Button btnConfirmRental;
    @FXML private TextField contact;
    @FXML private ComboBox<String> model;
    @FXML private TextField name;
    @FXML private TextArea txtRentalHistory;
    @FXML
    private Button return_btn;
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
    public void setClientDetails(Client client) {
        name.setText(client.getName());
        contact.setText(client.getContact());
        txtTotalPrice.setText(client.getTotalPrice());
        txtRentalHistory.setText(client.getRentalHistory());
    
        StartDate.setValue(LocalDate.parse(client.getStartDate()));
        EndDate.setValue(LocalDate.parse(client.getEndDate()));
    
        brand.setValue(client.getBrand());
        model.setValue(client.getModel());
    }
    
    @FXML
    void remove_client(ActionEvent event) {
        System.out.println(name.getText() + " has been removed from the database.");
        String sql = "DELETE FROM Clients WHERE name = ? AND contact_details = ?";
        try {
            Connection conn = DatabaseConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name.getText());
            stmt.setString(2, contact.getText());
            stmt.executeUpdate();
            System.out.println("Client removed from database.");
            if (clientController != null) {
                clientController.loadSampleData();
            }
            ((Stage) rm_btn.getScene().getWindow()).close();
        } catch (Exception e) {

        }
    }
    private ClientController clientController;

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }
    @FXML
    void EditClient(ActionEvent event) {
        if (StartDate.getValue() != null && EndDate.getValue() != null) {
            long daysBetween = ChronoUnit.DAYS.between(StartDate.getValue(), EndDate.getValue());

            String getClientSql = "SELECT client_id FROM Clients WHERE name = ?";
            String updateClientSql = "UPDATE Clients SET name = ?, contact_details = ? , rental_history = ?  WHERE client_id = ?";
            String updateRentalSql = "UPDATE Rentals SET start_date = ?, end_date = ?, total_cost = ?, brand = ?, model = ? WHERE client_id = ?";

            try (Connection conn = DatabaseConnection.connect();
                PreparedStatement getClientStmt = conn.prepareStatement(getClientSql)) {

                getClientStmt.setString(1, name.getText());
                ResultSet rs = getClientStmt.executeQuery();

                if (rs.next()) {
                    int clientId = rs.getInt("client_id");

                    // Update client info (optional)
                    try (PreparedStatement updateClientStmt = conn.prepareStatement(updateClientSql)) {
                        updateClientStmt.setString(1, name.getText());
                        updateClientStmt.setString(2, contact.getText());
                        updateClientStmt.setString(3, txtRentalHistory.getText());
                        updateClientStmt.setInt(4, clientId);
                        updateClientStmt.executeUpdate();
                    }

                    // Update rental info
                    try (PreparedStatement updateRentalStmt = conn.prepareStatement(updateRentalSql)) {
                        updateRentalStmt.setString(1, StartDate.getValue().toString());
                        updateRentalStmt.setString(2, EndDate.getValue().toString());
                        updateRentalStmt.setDouble(3,  Double.parseDouble(txtTotalPrice.getText())); // Assuming rate is 10 per day
                        updateRentalStmt.setString(4, brand.getValue());
                        updateRentalStmt.setString(5, model.getValue());
                        updateRentalStmt.setInt(6, clientId);
                        updateRentalStmt.executeUpdate();

                        System.out.println("Client and rental info updated.");

                        if (clientController != null) {
                            clientController.loadSampleData();
                        }

                        ((Stage) btnConfirmRental.getScene().getWindow()).close();
                    }

                } else {
                    System.out.println("Client not found.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Please select both start and end dates.");
        }
        if (clientController != null) {
            clientController.loadSampleData();
        }else {
            System.out.println("ClientController is null");
        }
        
    }



    @FXML
    void get_thisCar_info(ActionEvent event) {

    }

    @FXML
    public void returned_car(ActionEvent event) {
        System.out.println("return car button clicked");
        addClient.updateVehicleStatus(model.getValue(), brand.getValue(), "Available");
       
        String query = "UPDATE Rentals SET status = returned WHERE model = ? AND brand = ?";
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, model.getValue());
            pstmt.setString(2, brand.getValue());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Cleared model, brand, and total_cost for: " + brand.getValue() + " " + model.getValue());
            } else {
                System.out.println("No matching record found to update for: " + brand.getValue() + " " + model.getValue());
            }
            } catch (SQLException e) {
                e.printStackTrace();
            }
           model.getItems().clear();
           brand.getItems().clear();
           
        if (clientController != null) {
            clientController.loadSampleData();
          }
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
    }


}
