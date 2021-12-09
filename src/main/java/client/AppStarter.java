package client;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class AppStarter extends Application {
   // private static Parent parent;
  // private WelcomeWindowController welcomeWindowController;
 //  private static final ClientNet clientNet = ClientNet.getClientNet();





    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("/welcomeWindow.fxml"));
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    /*    byte[] buffer = new byte[8192];
        InputStream is = clientNet.getPis();
            int read = is.read(buffer);
            String msg = new String(buffer, 0, read).trim();
            System.out.println(msg); */

    //   welcomeWindowController = new WelcomeWindowController();
      //  controller = new ClientController();
    }

  /*  public WelcomeWindowController getWelcomeWindowController() {
        return welcomeWindowController;
    } */


     /*      public static void enterApp() throws IOException {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(AppStarter.class.getResource("/com/geekbrains/file_storage/mainWindow.fxml"));
                parent = loader.load();
                Stage stage = new Stage();
                Scene scene = new Scene(parent);
                stage.setScene(scene);
                stage.show();
    } */




}

// /com/geekbrains/file_storage/file_storage.fxml

//  /com/geekbrains/file_storage/welcome_window.fxml
