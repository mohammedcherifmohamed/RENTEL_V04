package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import models.Client;

public class ClientController {
    @FXML
    private Button AddClient;

    @FXML
    private TableColumn<Client, String> brandColumn;

    @FXML
    private TableView<Client> carTable;

    @FXML
    private TableColumn<Client, String> contactColumn;

    @FXML
    private TableColumn<Client, String> end_date;

    @FXML
    private TableColumn<Client, String> modelColumn;

    @FXML
    private TableColumn<Client, String> nameColumn;

    @FXML
    private TextField searchBar;

    @FXML
    private TableColumn<Client, String> start_date;

    @FXML
    private TableColumn<Client, String> total_price;

    // ObservableList to store client data
    private ObservableList<Client> clientList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadSampleData();  // Add sample data when the app starts
    }

    private void setupTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        start_date.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        end_date.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        total_price.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        carTable.setItems(clientList);
    }

    // Load some initial data into the table
    public void loadSampleData() {
        String sql = "SELECT name, contact_details FROM Clients";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
    
            clientList.clear(); // Clear existing data before loading new ones
    
            while (rs.next()) {
                String name = rs.getString("name");
                String contact = rs.getString("contact_details");
    
                clientList.add(new Client(name, contact));
            }
    
            carTable.refresh(); // Refresh table view after updating data
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Add a new client when the button is clicked
    @FXML
void AddClientbtn(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/addClient.fxml"));
        Parent root = loader.load();

        // Get controller of addClient
        addClient addClientController = loader.getController();
        addClientController.setClientController(this); // Pass reference

        Stage stage = new Stage();
        stage.setTitle("Add Client");
        stage.setScene(new Scene(root));

        stage.showAndWait(); // Wait until closed, then refresh data

        loadSampleData(); // Refresh the table after dialog closes

    } catch (IOException e) {
        e.printStackTrace();
    }
}

}
