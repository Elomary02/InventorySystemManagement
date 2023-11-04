package com.example.gestioninventairemagasin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Button btn_login;
    @FXML
    private Button btn_signUp;

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btn_login.setOnAction(event -> openLoginWindow());
        btn_signUp.setOnAction(event -> openSignUpWindow());
    }

    private void openLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.setTitle("Login");
            primaryStage.close();
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openSignUpWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("signUp.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage signUpStage = new Stage();
            signUpStage.setScene(scene);
            signUpStage.setTitle("Sign Up");
            primaryStage.close();
            signUpStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}