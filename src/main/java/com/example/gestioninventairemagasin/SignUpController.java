package com.example.gestioninventairemagasin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {

    @FXML
    private TextField tf_fullName;
    @FXML
    private TextField tf_username;
    @FXML
    private TextField tf_password;
    @FXML
    private TextField tf_password_confirm;
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
        btn_signUp.setOnAction(event -> verifyInfosAndDisplayLogin());
    }

    private boolean isUsernameTaken(String username) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection("jdbc:sqlite:/C:\\Users\\aa\\SQLite\\sqlite-tools-win32-x86-3430200\\db_manip.db");
            String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
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

    private boolean queryDatabase(String fullName, String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection("jdbc:sqlite:/C:\\Users\\aa\\SQLite\\sqlite-tools-win32-x86-3430200\\db_manip.db");
            String sql = "INSERT INTO users (fn, username, password) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, fullName);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
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

    private void verifyInfosAndDisplayLogin() {
        String fullName = tf_fullName.getText();
        String username = tf_username.getText();
        String password = tf_password.getText();
        String passwordConfirm = tf_password_confirm.getText();

        if (isUsernameTaken(username)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Sign Up");
            alert.setHeaderText("Username is already taken");
            alert.setContentText("Please choose a different username.");
            alert.showAndWait();
        } else if (!password.equals(passwordConfirm)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Sign Up");
            alert.setHeaderText("Passwords do not match");
            alert.setContentText("Please confirm your password correctly.");
            alert.showAndWait();
        } else {
            if (queryDatabase(fullName, username, password)) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                    Stage homeStage = new Stage();
                    homeStage.setScene(scene);
                    homeStage.setTitle("Login");
                    Stage currentStage = (Stage) tf_username.getScene().getWindow();
                    currentStage.close();
                    homeStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Sign Up");
                alert.setHeaderText("Error during sign up");
                alert.setContentText("Please try again later.");
                alert.showAndWait();
            }
        }
    }

    private void openLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.setTitle("Login");
            Stage currentStage = (Stage) tf_username.getScene().getWindow();
            currentStage.close();
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
