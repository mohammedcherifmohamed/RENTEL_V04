package controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class dashboardController implements Initializable {
    Connection connection ;
    @FXML private BorderPane bp;
    @FXML private VBox display_content;
    @FXML private Label lblTotalClients;
    @FXML private Label lblTotalIncome;
    @FXML private Label lblTotalRentals;
    @FXML private Label lblTotalVehicles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connection = DatabaseConnection.connect();
        if (connection != null) {
            System.out.println("Database connection established.");
        } else {
            System.out.println("Database connection failed.");
        }
        loadInformation();
    }
    
    public void loadInformation() {
        if (connection == null) {
            System.out.println("Database connection is null.");
            return;
        }
    
        String sql = "SELECT COUNT(*) FROM Vehicles";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
    
            if (rs.next()) {
                int totalVehicles = rs.getInt(1);  
                lblTotalVehicles.setText(String.valueOf(totalVehicles));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching total vehicles: " + e.getMessage());
        }
    
        String sqlRentals = "SELECT COUNT(*) FROM Rentals";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlRentals);
             ResultSet rs = pstmt.executeQuery()) {
    
            if (rs.next()) {
                int totalRentals = rs.getInt(1);
                lblTotalRentals.setText(String.valueOf(totalRentals));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching total rentals: " + e.getMessage());
        }
    
        String sqlClients = "SELECT COUNT(*) FROM Clients";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlClients);
             ResultSet rs = pstmt.executeQuery()) {
    
            if (rs.next()) {
                int totalClients = rs.getInt(1);
                lblTotalClients.setText(String.valueOf(totalClients));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching total clients: " + e.getMessage());
        }
    
    String sqlIncome = "SELECT SUM(TotalIncome) FROM performance_reports";
    try (Connection conn = DatabaseConnection.connect(); 
        PreparedStatement pstmt = conn.prepareStatement(sqlIncome);
        ResultSet rs = pstmt.executeQuery()) {

        if (rs.next()) {
            double totalIncome = rs.getDouble(1);
            lblTotalIncome.setText(String.format("%.2f", totalIncome));  
        }
    } catch (SQLException e) {
        System.out.println("Error fetching total income: " + e.getMessage());
    }

    }
    
}
