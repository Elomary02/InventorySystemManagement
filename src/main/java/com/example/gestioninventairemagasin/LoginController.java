package com.example.gestioninventairemagasin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField tf_username;
    @FXML
    private TextField tf_password;
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
        btn_login.setOnAction(event -> verifyInfosAndDisplayHome());
        btn_signUp.setOnAction(event -> openSignUpWindow());
    }

    private boolean queryDatabase(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection("jdbc:sqlite:/C:\\Users\\aa\\SQLite\\sqlite-tools-win32-x86-3430200\\db_manip.db");
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private void verifyInfosAndDisplayHome() {
        String username = tf_username.getText();
        String password = tf_password.getText();

        boolean isValidUser = queryDatabase(username, password);

        if (isValidUser) {
            UserData.getInstance().setUsername(username);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage homeStage = new Stage();
                homeStage.setScene(scene);
                homeStage.setTitle("Home");
                HomeController setWelTxt = loader.getController();
                setWelTxt.setWelcomeText("Welcome " + username + "!");
                HomeController setDate = loader.getController();
                setDate.setLabel_date(String.valueOf(LocalDate.now()));
                HomeController setSoldToday = loader.getController();
                setSoldToday.updateLabelSoldToday();
                HomeController setOutOfStock = loader.getController();
                setOutOfStock.updateLabelOutOfStock();
                HomeController setTotalCells = loader.getController();
                setTotalCells.updateLabelTotalSales();
                Stage currentStage = (Stage) tf_username.getScene().getWindow();
                currentStage.close();
                homeStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Login");
            alert.setHeaderText("Invalid Username or Password");
            alert.setContentText("Please enter a valid username and password to login.");
            alert.showAndWait();
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
            Stage currentStage = (Stage) tf_username.getScene().getWindow();
            currentStage.close();
            signUpStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
