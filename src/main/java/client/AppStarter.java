package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class AppStarter extends Application {

    private static Stage primaryStage;
    private static String previousWindow;



    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("/welcomeWindow.fxml"));
        this.primaryStage = primaryStage;
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }


    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static String getPreviousWindow() {
        return previousWindow;
    }

    public static void setPreviousWindow(String window) {
        previousWindow = window;
    }





}


