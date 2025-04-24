package controllers;

import database.DatabaseConnection;
import java.util.ResourceBundle;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    @FXML    private DatePicker EndDate;
    @FXML    private DatePicker StartDate;
    @FXML    private ComboBox<String> brand;
    @FXML    private Button btnConfirmRental;
    @FXML    private TextField contact;
    @FXML    private ComboBox<String> model;
    @FXML    private TextField name;
    @FXML    private TextArea txtRentalHistory;
    @FXML    private TextField txtTotalPrice;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("from initialize");
        String sql = "SELECT DISTINCT brand FROM Vehicles WHERE availability_status = 'Available'"; 
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
        String sql = "SELECT model FROM Vehicles WHERE brand = ?  AND availability_status = 'Available'";

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

            String checkSql = "SELECT client_id FROM Clients WHERE name = ? AND contact_details = ?";
            String insertClientSql = "INSERT INTO Clients (name, contact_details,rental_history) VALUES (?, ?,?)";
            String insertRentalSql = "INSERT INTO Rentals ( client_id, start_date, end_date, total_cost,brand,model) VALUES ( ?,  ?, ?, ?,?,?)";
            String insertPerformanceSql = "INSERT INTO performance_reports ( carbrand, carModel, totalRent, TotalIncome) VALUES ( ?, ?, ?, ?)";

            String category = getCategoryForVehicle(brand.getValue(), model.getValue());
            double totalPrice = Double.parseDouble(txtTotalPrice.getText());
            updatePerformanceReports(brand.getValue(), model.getValue(), totalPrice, category);

            updateVehicleStatus( model.getValue() ,  brand.getValue(),  "Rented");
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

                checkStmt.setString(1, name.getText());
                checkStmt.setString(2, contact.getText());
                ResultSet rs = checkStmt.executeQuery();

                int clientId;

                if (rs.next()) {
                    clientId = rs.getInt("client_id");
                    System.out.println("Client already exists.");
                } else {
                    try (PreparedStatement insertClientStmt = conn.prepareStatement(insertClientSql, Statement.RETURN_GENERATED_KEYS)) {
                        insertClientStmt.setString(1, name.getText());
                        insertClientStmt.setString(2, contact.getText());
                        insertClientStmt.setString(3, txtRentalHistory.getText());
                        insertClientStmt.executeUpdate();

                        ResultSet generatedKeys = insertClientStmt.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            clientId = generatedKeys.getInt(1);
                            System.out.println("New client added with ID: " + clientId);
                        } else {
                            throw new SQLException("Creating client failed, no ID obtained.");
                        }
                    }
                }

                // Insert into Rentals
                try (PreparedStatement insertRentalStmt = conn.prepareStatement(insertRentalSql)) {
                    insertRentalStmt.setInt(1, clientId);
                    insertRentalStmt.setString(2, StartDate.getValue().toString());
                    insertRentalStmt.setString(3, EndDate.getValue().toString());
                    insertRentalStmt.setDouble(4, daysBetween * Double.parseDouble(txtTotalPrice.getText()) ); 
                    insertRentalStmt.setString(5, brand.getValue());
                    insertRentalStmt.setString(6, model.getValue());
                    insertRentalStmt.executeUpdate();

                    if (clientController != null) {
                        clientController.loadSampleData();
                    }

                    ((Stage) btnConfirmRental.getScene().getWindow()).close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Please select both start and end dates.");
        }
    }

    private String getCategoryForVehicle(String brand, String model) {
        String sql = "SELECT category FROM Vehicles WHERE brand = ? AND model = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, brand);
            stmt.setString(2, model);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("category");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void updatePerformanceReports(String brand, String model, double totalIncome , String category) {
        String selectQuery = "SELECT * FROM performance_reports WHERE carbrand = ? AND carModel = ?";
        String updateQuery = "UPDATE performance_reports SET totalRent = totalRent + 1, TotalIncome = TotalIncome + ? WHERE carbrand = ? AND carModel = ?";
        String insertQuery = "INSERT INTO performance_reports (carbrand, carModel, totalRent, TotalIncome,category) VALUES (?, ?, ?, ?,?)";
    
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
    
            selectStmt.setString(1, brand);
            selectStmt.setString(2, model);
            ResultSet rs = selectStmt.executeQuery();
    
            if (rs.next()) {
                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                    updateStmt.setDouble(1, totalIncome);
                    updateStmt.setString(2, brand);
                    updateStmt.setString(3, model);
                    updateStmt.executeUpdate();
                }
            } else {
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, brand);
                    insertStmt.setString(2, model);
                    insertStmt.setInt(3, 1);
                    insertStmt.setDouble(4, totalIncome);
                    insertStmt.setString(5, category);
                    insertStmt.executeUpdate();
                }
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    public static void updateVehicleStatus(String model, String brand, String newStatus) {
        String query = "UPDATE Vehicles SET availability_status = ? WHERE brand = ? AND model = ?";
    
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setString(2, brand);
            stmt.setString(3, model);
            stmt.executeUpdate();
            System.out.println("Vehicle status updated to: " + newStatus);
        } catch (SQLException e) {
            e.printStackTrace();
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