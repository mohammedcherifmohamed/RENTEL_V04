package controllers;
import database.DatabaseConnection;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class register_controller {
    @FXML private PasswordField confirmPaswword;
    @FXML private TextField email;
    @FXML private Label err_msg;
    @FXML private ImageView imageView;
    @FXML private Button login;
    @FXML private VBox main_rightbox;
    @FXML private TextField name;
    @FXML private PasswordField paswword;
    @FXML private Button register;
    @FXML private VBox rightbox;
    @FXML

    void go_login(ActionEvent event) {
             try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void register(ActionEvent event) {
        System.out.println(name.getText() +" "+name.getText() +" "+  email.getText()+" "+ paswword.getText()+" "+confirmPaswword.getText());
        if(paswword.getText().equals(confirmPaswword.getText()) && !DatabaseConnection.isExists( name.getText(),  email.getText()) ){

            DatabaseConnection.AddUser(name.getText(), name.getText(), email.getText(), paswword.getText());
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                
                stage.setScene(new Scene(root));
                stage.setTitle("Login");
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
         else{
            if(DatabaseConnection.isExists( name.getText(),  email.getText()))
                err_msg.setText("User Already Exists");
            if(!paswword.getText().equals(confirmPaswword.getText()) )
                err_msg.setText("Password Does Not Match");

        }

    }
    
}
