package client;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UploadWindowController implements Initializable {
    private static final ClientNet clientNet = ClientNet.getClientNet();
    private Path path;

    private Stage stage;
    private Scene scene;
    private Parent root;


    @FXML
    public Label currentFolder;

    @FXML
    public ListView<String> fileList;


    private void fillListView(Path path) {
        try {
            int quantityOfFiles = Files.list(path).collect(Collectors.toList()).size();
            if (quantityOfFiles > 0) {
                List<String> files = Files.list(path).map(file -> file.getFileName().toString()).collect(Collectors.toList());
               for(String file : files) {
                   fileList.getItems().add(file);
               }
            }
        }catch(IOException e){
                e.printStackTrace();
            }
        }

        @Override
    public void initialize(URL location, ResourceBundle resources) {
            if(AppStarter.getCurrentFolder() == null) {
                path = Paths.get("client_files");
                AppStarter.setCurrentFolder(path.toAbsolutePath());
            } else {
                path = AppStarter.getCurrentFolder();
            }
        currentFolder.setText(path.toAbsolutePath().toString());
        fillListView(path);
        AppStarter.setPreviousWindow("uploadWindow");
    }

    public void directoryUpButton(ActionEvent actionEvent) {
        path = path.toAbsolutePath().getParent();
        AppStarter.setCurrentFolder(path);
        currentFolder.setText(path.toString());
        fileList.getItems().clear();
        fillListView(path);
    }

    public void directoryDownButton(ActionEvent actionEvent) {
        String item = fileList.getSelectionModel().getSelectedItem();
        Path pathToItem = Paths.get(path.toAbsolutePath() + "\\" + item);
        if(Files.isDirectory(pathToItem)) {
            path = pathToItem;
            AppStarter.setCurrentFolder(path);
            currentFolder.setText(path.toString());
            fileList.getItems().clear();
            fillListView(pathToItem);
        } else {
            try {
                root = FXMLLoader.load(getClass().getResource("/isNotDirectoryWindow.fxml"));
                stage = AppStarter.getPrimaryStage();
                scene = new Scene(root);
                stage.setScene(scene);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void returnButton(ActionEvent actionEvent) {
        try {
            root = FXMLLoader.load(getClass().getResource("/mainWindow.fxml"));
            stage = AppStarter.getPrimaryStage();
            scene = new Scene(root);
            stage.setScene(scene);
        }catch (IOException e) {
            e.printStackTrace();
        }
        AppStarter.setCurrentFolder(null);
    }

    public void uploadButton(ActionEvent actionEvent) {
        String item = fileList.getSelectionModel().getSelectedItem();
        Path pathToItem = Paths.get(path.toAbsolutePath() + "\\" + item);
        if(!Files.isDirectory(pathToItem)) {
            clientNet.sendFile(pathToItem);
        } else {
            try {
                root = FXMLLoader.load(getClass().getResource("/isNotFileWindow.fxml"));
                stage = AppStarter.getPrimaryStage();
                scene = new Scene(root);
                stage.setScene(scene);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

