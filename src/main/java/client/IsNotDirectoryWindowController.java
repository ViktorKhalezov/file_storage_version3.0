package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class IsNotDirectoryWindowController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;


    public void окButton(ActionEvent actionEvent) {
        try {
            if(AppStarter.getPreviousWindow().equals("mainWindow")) {
                root = FXMLLoader.load(getClass().getResource("/mainWindow.fxml"));
            }
            if(AppStarter.getPreviousWindow().equals("uploadWindow")) {
                root = FXMLLoader.load(getClass().getResource("/uploadFileWindow.fxml"));
            }
            if(AppStarter.getPreviousWindow().equals("downloadWindow")) {
                root = FXMLLoader.load(getClass().getResource("/directoryForDownloadWindow.fxml"));
            }
            stage = AppStarter.getPrimaryStage();
            scene = new Scene(root);
            stage.setScene(scene);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
