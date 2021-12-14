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

public class IncorrectNameWindowController implements Initializable {
    private static final ClientNet clientNet = ClientNet.getClientNet();

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    Label label;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String command = clientNet.getIncorrectNameCommand();
        if(command.equals("incorrectSigns")) {
            label.setText("Имя файла или директории не должно содержать знаков: \\ / : * ? \" < > |");
            clientNet.setIncorrectNameCommand(null);
        }
        if(command.equals("objectExists")) {
            label.setText("Объект с таким именем уже существует. Введите другое имя.");
            clientNet.setIncorrectNameCommand(null);
        }
    }

    public void okButton(ActionEvent actionEvent) {
        try {
            root = FXMLLoader.load(getClass().getResource("/newObjectNameWindow.fxml"));
            stage = AppStarter.getPrimaryStage();
            scene = new Scene(root);
            stage.setScene(scene);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


}

  //  Имя файла или директории не должно содержать знаков: \ / : * ? " < > |