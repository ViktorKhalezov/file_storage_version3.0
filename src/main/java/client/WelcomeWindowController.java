package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class WelcomeWindowController implements Initializable {
    private static final ClientNet clientNet = ClientNet.getClientNet();
   // private String message;

    @FXML
    public javafx.scene.control.TextField login;

    @FXML
    public PasswordField password;

    public Button enter;


 /*   public ClientNet getClientNet(){
        return clientNet;
    }


    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    } */


    public void enterButton(ActionEvent actionEvent) {
        clientNet.sendMessage(login.getText());
            while (true) {
                if(clientNet.isAuthorized() == true) {
                    break;
                }
            }
            enterMainWindow();
       }



    public String readMessage(String message) {
        return message;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    public void enterMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppStarter.class.getResource("/mainWindow.fxml"));
            Pane mainWindow  = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(mainWindow);
            stage.setScene(scene);
            stage.show();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


}
