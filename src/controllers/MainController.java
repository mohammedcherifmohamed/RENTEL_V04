package controllers;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.image.ImageView;

import database.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;

public class MainController implements Initializable {

    @FXML private Button Clients;
    @FXML    private BorderPane bp;

    @FXML   private VBox display_content;

    @FXML private Label lblTotalClients;

    @FXML private Label lblTotalIncome;

    @FXML private Label lblTotalRentals;

    @FXML private Label lblTotalVehicles;
    @FXML private Button logout;
    @FXML private ImageView notification_icon;
    @FXML private Label notification_label;
    @FXML private Label user_label;
    @FXML private StackPane popupContainer;
    @FXML private ListView<String> notificationList;
    private PopOver popOver;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        display_content.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
            Parent dashboardView = loader.load();
    
            display_content.getChildren().clear();
            display_content.getChildren().add(dashboardView); 
        } catch (IOException e) {
            e.printStackTrace();
        }
                // You can initialize once here
                VBox popContent = new VBox(10);
                popContent.setStyle("-fx-background-color: white; -fx-padding: 15;");
        
                Label notif1 = new Label("🔔 Reminder: Return vehicle by 5 PM");
                Label notif2 = new Label("🚗 New vehicle added to fleet");
                Label notif3 = new Label("👤 New client registered");
                Label notif4 = new Label("👤 New client registered");
                Label notif5 = new Label("👤 New client registered");
                Label notif6 = new Label("👤 New client registered");
                Label notif7 = new Label("👤 New client registered");
                Label notif8 = new Label("👤 New client registered");
        
                popContent.getChildren().addAll(notif1, notif2, notif3, notif4, notif5, notif6, notif7, notif8);
                popOver = new PopOver(popContent);
                popOver.setArrowLocation(PopOver.ArrowLocation.RIGHT_CENTER);
                popOver.setDetachable(false);
                popOver.setAutoHide(true);
    }

    @FXML
    void go_dashboard(ActionEvent event) {
        System.out.println("go_dashboard");
        display_content.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
            Parent dashboardView = loader.load();
    
            display_content.getChildren().clear();
            display_content.getChildren().add(dashboardView); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void go_reports(ActionEvent event) {
        System.out.println("go_reports");

    }

    @FXML
    void go_vehicles(ActionEvent event) {
        System.out.println("go_vehicles");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/carsSection.fxml")); // Adjust the path
            Parent carsView = loader.load();
    
            display_content.getChildren().clear(); // Clear previous content
            display_content.getChildren().add(carsView); // Add the new content
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void go_clients(ActionEvent event) {
        System.out.println("Clients Display");
       try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Clients.fxml")); // Adjust the path
        Parent clientsView = loader.load();

        display_content.getChildren().clear(); // Clear previous content
        display_content.getChildren().add(clientsView); // Add the new content
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void see_notification(MouseEvent  event) {
        System.out.println("see_notification");
        if (popOver.isShowing()) {
            popOver.hide();
        } else {
            popOver.show(notification_icon);
        }
    }
    
    @FXML
    void lougout(ActionEvent event) {
        System.out.println("Logout");
       try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
        Parent root = loader.load();


        Stage stage = new Stage();
        stage.setTitle("Login");
        stage.setScene(new Scene(root));
        stage.show();
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            
            // e.printStackTrace();
        }
    } 
    public void setUserName(String userName) {
        this.user_label.setText(userName);
    }
}
