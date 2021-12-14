package client;


import common.ChangeDirectoryMessage;
import common.CheckStatusMessage;
import common.DeleteFileMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

@Slf4j
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
        currentFolder.setText(clientNet.getServerFolderName());
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
        if(item != null) {
            clientNet.sendMessage(new CheckStatusMessage(item));
            new Thread(() -> {
                while (true) {
                    if (clientNet.getStatus() != null) {
                        break;
                    }
                }
            }).start();
            try {
                Thread.currentThread().sleep(200);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
            String status = clientNet.getStatus();
            if (status.equals("isFile")) {
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

    public void directoryUpButton(ActionEvent actionEvent) {
        clientNet.sendMessage(new ChangeDirectoryMessage("up", null));
        try {
            Thread.currentThread().sleep(200);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        serverFileList.getItems().clear();
        fillListView();
    }

    public void directoryDownButton(ActionEvent actionEvent) {
        String item = serverFileList.getSelectionModel().getSelectedItem();
        clientNet.sendMessage(new CheckStatusMessage(item));
        new Thread(() -> {
        while (true) {
            if (clientNet.getStatus() != null) {
                break;
            }
        }
       }).start();
        try {
            Thread.currentThread().sleep(200);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        String status = clientNet.getStatus();
            if(status.equals("isDirectory")) {
                clientNet.sendMessage(new ChangeDirectoryMessage("down", item));
                try {
                    Thread.currentThread().sleep(200);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                serverFileList.getItems().clear();
                fillListView();
            } else {
                try {
                    root = FXMLLoader.load(getClass().getResource("/isNotDirectoryWindow.fxml"));
                    stage = AppStarter.getPrimaryStage();
                    scene = new Scene(root);
                    stage.setScene(scene);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    public void renameFileButton(ActionEvent actionEvent) {
        clientNet.setCommand("renameFile");
        String fileForRename = serverFileList.getSelectionModel().getSelectedItem();

        if(fileForRename != null) {
            clientNet.setFileForRename(fileForRename);
            try {
                root = FXMLLoader.load(getClass().getResource("/newObjectNameWindow.fxml"));
                stage = AppStarter.getPrimaryStage();
                scene = new Scene(root);
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createDirectoryButton(ActionEvent actionEvent) {
        clientNet.setCommand("createDirectory");
        try {
            root = FXMLLoader.load(getClass().getResource("/newObjectNameWindow.fxml"));
            stage = AppStarter.getPrimaryStage();
            scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFileButton(ActionEvent actionEvent) {
        String item = serverFileList.getSelectionModel().getSelectedItem();
        clientNet.sendMessage(new DeleteFileMessage(item));
        new Thread( () -> {
            while (true) {
                if (clientNet.isOperationConfirmed() != null) {
                    break;
                }
            }
        }).start();
        try {
            Thread.currentThread().sleep(200);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        if(clientNet.isOperationConfirmed() == true) {
            serverFileList.getItems().clear();
            fillListView();
        } else {
            try {
                root = FXMLLoader.load(getClass().getResource("/deleteFileErrorWindow.fxml"));
                stage = AppStarter.getPrimaryStage();
                scene = new Scene(root);
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
            clientNet.setOperationConfirmed(null);
        }
    }

}
