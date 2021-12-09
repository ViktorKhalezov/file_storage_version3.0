package client;

import io.netty.channel.socket.SocketChannel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainWindowController implements Initializable {
    private static final ClientNet clientNet = ClientNet.getClientNet();

    @FXML
    public ListView<String> serverFilesListView;


    public ListView<String> getServerFilesListView() {
        return serverFilesListView;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        /*   ArrayList<String> fileList = clientNet.getServerFileList();

        for(String file : fileList) {
            serverFilesListView.getItems().add(file);
        } */
    }
        // serverFilesListView = new ListView<>();


    /*    //  net = WelcomeWindowController.clientNet;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(AppStarter.class.getResource("/com/geekbrains/file_storage/welcomeWindow.fxml"));
     /*   try {
            loader.load(getClass().getResource("/com/geekbrains/file_storage/welcomeWindow.fxml"));
        }catch (IOException e) {
            e.printStackTrace();
        } */
 //      WelcomeWindowController controller = loader.getController();
//     //   Parent root = fxmlLoader.load(getClass().getResource(path).openStream());
     //   RootController controller = fxmlLoader.getController();

 //        for(int i = 0; i < 9; i++) {
 //       serverFilesListView.getItems().add(controller.getMessage());
  //       }
        /*  Path path = Paths.get("server_files");
        int quantityOfFiles = 0;
        try {
            quantityOfFiles = Files.list(path).collect(Collectors.toList()).size();
        if(quantityOfFiles > 0) {
            List<String> fileList = Files.list(path).map(file -> String.valueOf(file.getFileName())).collect(Collectors.toList());
            fileList.forEach(file -> serverFilesListView.getItems().add(file));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } */

}

 /* loader.setLocation(AppStarter.class.getResource("/com/geekbrains/file_storage/mainWindow.fxml"));
        Pane mainWindow  = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(mainWindow);
        stage.setScene(scene); */