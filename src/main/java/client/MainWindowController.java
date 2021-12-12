package client;


import common.CheckStatusMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class MainWindowController implements Initializable {
    private static final ClientNet clientNet = ClientNet.getClientNet();
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public Label currentFolder;

    @FXML
    public ListView<String> serverFileList;


    public void fillListView() {
        currentFolder.setText(clientNet.getFolderName());
        ArrayList<String> fileList = clientNet.getServerFileList();
        if (fileList.size() > 0) {
            for (int i = 0; i < fileList.size(); i++) {
                 serverFileList.getItems().add(fileList.get(i));
            }
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources){
        fillListView();
        AppStarter.setPreviousWindow("mainWindow");
        }

    public void uploadButton(ActionEvent actionEvent) {
        try {
            root = FXMLLoader.load(getClass().getResource("/uploadFileWindow.fxml"));
            stage = AppStarter.getPrimaryStage();
            scene = new Scene(root);
            stage.setScene(scene);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void downloadButton(ActionEvent actionEvent) {
        String item = serverFileList.getSelectionModel().getSelectedItem();
        clientNet.sendMessage(new CheckStatusMessage(item));
        try {
            Thread.currentThread().sleep(300);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        String status = clientNet.getStatus();
       if(status != null) {
        if(status.equals("isFile")) {
           clientNet.setFileForDownload(item);
           try {
               root = FXMLLoader.load(getClass().getResource("/directoryForDownloadWindow.fxml"));
               stage = AppStarter.getPrimaryStage();
               scene = new Scene(root);
               stage.setScene(scene);
           } catch (IOException e) {
               e.printStackTrace();
           }
       } else {
            try {
                root = FXMLLoader.load(getClass().getResource("/isNotFileWindow.fxml"));
                stage = AppStarter.getPrimaryStage();
                scene = new Scene(root);
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       }
    }


}
