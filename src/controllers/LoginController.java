package controllers;
import database.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;

public class LoginController {

    @FXML private Label err_msg;
    @FXML private ImageView imageView;
    @FXML private ImageView imageView1;
    @FXML private Button login_btn;
    @FXML private VBox main_rightbox;
    @FXML private TextField name;
    @FXML private TextField password;
    @FXML private Button register;
    
    @FXML
    public void login(ActionEvent event) {
        String userName = name.getText();
        String pass = password.getText();

        if (DatabaseConnection.validateAgent(userName, pass)) {
            err_msg.setText("Login successful!"); 
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
                Parent root = loader.load();
        
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                MainController controller = loader.getController();

                controller.setUserName(userName);
        
                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard");
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid credentials!");
            err_msg.setText("Invalid email or password.");
        }

    }
    
    @FXML
    public void register(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/register.fxml"));
            Parent root = loader.load();
    
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    
            stage.setScene(new Scene(root));
            stage.setTitle("Register");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
