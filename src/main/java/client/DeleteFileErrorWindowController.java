package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DeleteFileErrorWindowController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void okButton(ActionEvent actionEvent) {
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
