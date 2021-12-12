package client;

import common.AuthMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class WelcomeWindowController implements Initializable {

    private static final ClientNet clientNet = ClientNet.getClientNet();
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public javafx.scene.control.TextField login;

    @FXML
    public PasswordField password;


    public void enterButton(ActionEvent actionEvent) {
        AuthMessage authMessage = new AuthMessage(login.getText(), password.getText());
        clientNet.sendMessage(authMessage);
        new Thread(() -> {
            while (true) {
                if (clientNet.isAuthorized() == true && clientNet.getServerFileList() != null) {
                    break;
                }
            }
        }).start();
        try {
            Thread.currentThread().sleep(300);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        if (clientNet.isAuthorized() == true && clientNet.getServerFileList() != null) {
            enterMainWindow();
        }

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    public void enterMainWindow() {
        try {
            root = FXMLLoader.load(getClass().getResource("/mainWindow.fxml"));
            stage = AppStarter.getPrimaryStage();
            scene = new Scene(root);
            stage.setScene(scene);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


}
