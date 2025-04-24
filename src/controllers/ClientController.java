package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
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

    private ObservableList<Client> clientList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadSampleData();
        setupRowClick();

    }
    private void setupRowClick() {
        carTable.setRowFactory(tv -> {
            TableRow<Client> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    System.out.println("Row clicked: " + row.getItem());

                    Client clickedClient = row.getItem();
                    showClientDetails(clickedClient);
                }
            });
            return row;
        });
    }
    private void showClientDetails(Client client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ClientDetails.fxml"));
            Parent root = loader.load();
    
            // Get the controller instance
            ClientDetailsController controller = loader.getController();
            controller.setClientDetails(client);  
            controller.setClientController(this); 

    
            Stage stage = new Stage();
            stage.setTitle("Client Details");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
   
    public void loadSampleData() {
        String sql = "SELECT c.name, c.contact_details, c.rental_history , r.start_date, r.end_date, r.total_cost , r.model , r.brand " +
                     "FROM Clients c " +
                     "JOIN Rentals r ON c.client_id = r.client_id " ;
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
             clientList.clear(); 

            while (rs.next()) {
    
                String name = rs.getString("name");
                String contact = rs.getString("contact_details");
                String brand = rs.getString("brand");
                String model = rs.getString("model");
                String start = rs.getString("start_date");
                String end = rs.getString("end_date");
                String rental_history = rs.getString("rental_history");
                String totalCost = String.valueOf(rs.getDouble("total_cost"));
                clientList.add(new Client(name,contact,brand, model, start, end,totalCost, rental_history));

            }
            carTable.refresh(); 
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void search(KeyEvent event) {
        String searchValue = searchBar.getText();

        String sql = "SELECT c.name, c.contact_details, c.rental_history, r.start_date, r.end_date, r.total_cost, r.model, r.brand " +
                    "FROM Clients c " +
                    "JOIN Rentals r ON c.client_id = r.client_id";

        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            clientList.clear();

            while (rs.next()) {
                String name = rs.getString("name");
                String contact = rs.getString("contact_details");
                String brand = rs.getString("brand");
                String model = rs.getString("model");
                String start = rs.getString("start_date");
                String end = rs.getString("end_date");
                String totalCost = rs.getString("total_cost") ;
                String rental_history = rs.getString("rental_history");

                if (contact.toLowerCase().contains(searchValue.toLowerCase())) {
                    clientList.add(new Client(name, contact, brand, model, start, end, totalCost, rental_history));
                }
            }
            carTable.refresh();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }   

    @FXML
    void AddClientbtn(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/addClient.fxml"));
            Parent root = loader.load();

            addClient addClientController = loader.getController();
            addClientController.setClientController(this); 

            Stage stage = new Stage();
            stage.setTitle("Add Client");
            stage.setScene(new Scene(root));

            stage.showAndWait(); 
            loadSampleData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
