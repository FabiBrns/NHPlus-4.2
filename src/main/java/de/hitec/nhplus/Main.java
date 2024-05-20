package de.hitec.nhplus;

import de.hitec.nhplus.datastorage.ConnectionBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        Main.primaryStage = primaryStage;
        Main.primaryStage.setTitle("NHPlus");
        Main.primaryStage.setResizable(false);

        Main.primaryStage.setOnCloseRequest(event -> {
            ConnectionBuilder.closeConnection();
            Platform.exit();
            System.exit(0);
        });

        authenticationWindow();
    }

    public static void authenticationWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/AuthenticationView.fxml"));
            AnchorPane pane = loader.load();

            Scene scene = new Scene(pane);

            Main.primaryStage.setScene(scene);
            Main.primaryStage.show();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void mainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/MainWindowView.fxml"));
            BorderPane pane = loader.load();

            Scene scene = new Scene(pane);

            Main.primaryStage.setScene(scene);
            Main.primaryStage.show();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}