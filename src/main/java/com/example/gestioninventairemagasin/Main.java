package com.example.gestioninventairemagasin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();

        MainController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);

        primaryStage.setTitle("Your Inventory System Manager");
        primaryStage.setScene(new Scene(root, 600, 350));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}