 package client;

 import common.CreateDirectoryMessage;
 import common.RenameFileMessage;
 import javafx.event.ActionEvent;
 import javafx.fxml.FXML;
 import javafx.fxml.FXMLLoader;
 import javafx.fxml.Initializable;
 import javafx.scene.Parent;
 import javafx.scene.Scene;
 import javafx.scene.control.Label;
 import javafx.scene.control.TextField;
 import javafx.stage.Stage;
 import java.io.IOException;
 import java.net.URL;
 import java.util.ResourceBundle;

 public class NewObjectNameWindowController implements Initializable {
     private static final ClientNet clientNet = ClientNet.getClientNet();

     private Stage stage;
     private Scene scene;
     private Parent root;

     @FXML
     public Label label;

     @FXML
     public TextField newName;

     @Override
     public void initialize(URL location, ResourceBundle resources) {
         String command = clientNet.getCommand();
         if(command.equals("renameFile")) {
            label.setText("Введите новое имя файла:");
         }
         if(command.equals("createDirectory")) {
             label.setText("Введите имя для новой директории:");
         }
     }


     public void okButton(ActionEvent actionEvent) {
         String name = newName.getText();
         if(name.contains("\\") || name.contains("/") || name.contains(":") || name.contains("*") || name.contains("?")
                 || name.contains("\"") || name.contains("<") || name.contains(">") || name.contains("|")) {
             clientNet.setIncorrectNameCommand("incorrectSigns");
             try {
                 root = FXMLLoader.load(getClass().getResource("/incorrectNameWindow.fxml"));
                 stage = AppStarter.getPrimaryStage();
                 scene = new Scene(root);
                 stage.setScene(scene);
             } catch (IOException e) {
                 e.printStackTrace();
             }
         } else {
             String command = clientNet.getCommand();
             if (command.equals("renameFile")) {
                 clientNet.sendMessage(new RenameFileMessage(clientNet.getFileForRename(), name));
             }
             if (command.equals("createDirectory")) {
                 clientNet.sendMessage(new CreateDirectoryMessage(name));
             }
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
                 clientNet.setOperationConfirmed(null);
                 try {
                     clientNet.setFileForRename(null);
                     root = FXMLLoader.load(getClass().getResource("/mainWindow.fxml"));
                     stage = AppStarter.getPrimaryStage();
                     scene = new Scene(root);
                     stage.setScene(scene);
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             } else {
                 clientNet.setOperationConfirmed(null);
                 clientNet.setIncorrectNameCommand("objectExists");
                 try {
                     root = FXMLLoader.load(getClass().getResource("/incorrectNameWindow.fxml"));
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
