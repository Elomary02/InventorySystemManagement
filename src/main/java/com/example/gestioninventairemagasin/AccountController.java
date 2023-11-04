package com.example.gestioninventairemagasin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AccountController implements Initializable {

    @FXML
    private Button btn_home;
    @FXML
    private Button btn_items;
    @FXML
    private Button btn_logout;
    @FXML
    private Button btn_acc;
    @FXML
    private Button btn_update;
    @FXML
    private TextField tf_fullName;
    @FXML
    private TextField tf_username;
    @FXML
    private TextField tf_password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setFnTf(getUserFnByUsername(UserData.getInstance().getUsername()));
        setUnTf(UserData.getInstance().getUsername());
        setPassTf(getUserPassByUsername(UserData.getInstance().getUsername()));
        btn_home.setOnAction(event -> openDashboardWindow());
        btn_items.setOnAction(event -> openItemsWindow());
        btn_acc.setOnAction(event -> openAccWindow());
        btn_logout.setOnAction(event -> openLoginWindow());
        btn_update.setOnAction(event -> updateUserInfosInDb());
    }

    private void updateUserInfosInDb(){
        String newFullName = tf_fullName.getText();
        String newUsername = tf_username.getText();
        String newPassword = tf_password.getText();

        if (newFullName.isEmpty() || newUsername.isEmpty() || newPassword.isEmpty()) {
            showErrorDialog("Please fill in all fields.");
            return;
        }

        if (!newUsername.equals(UserData.getInstance().getUsername())) {
            if (isUsernameTaken(newUsername)) {
                showErrorDialog("Username already taken. Please choose a different username.");
                return;
            }
        }

        if (updateUserInDatabase(newFullName, newUsername, newPassword)) {
            openLoginWindow();
        } else {
            showErrorDialog("Error updating user information. Please try again.");
        }
    }

    private boolean isUsernameTaken(String newUsername) {
        java.sql.Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:/C:\\Users\\aa\\SQLite\\sqlite-tools-win32-x86-3430200\\db_manip.db");
            String sql = "SELECT username FROM users WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newUsername);
            ResultSet resultSet = pstmt.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                pstmt.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean updateUserInDatabase(String newFullName, String newUsername, String newPassword) {
        java.sql.Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:/C:\\Users\\aa\\SQLite\\sqlite-tools-win32-x86-3430200\\db_manip.db");
            String sql = "UPDATE users SET fn = ?, username = ?, password = ? WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newFullName);
            pstmt.setString(2, newUsername);
            pstmt.setString(3, newPassword);
            pstmt.setString(4, UserData.getInstance().getUsername());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                pstmt.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error Occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openDashboardWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.setTitle("Dashboard");
            Stage currentStage = (Stage) btn_home.getScene().getWindow();
            currentStage.close();
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openItemsWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("products.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.setTitle("Products Manager");
            Stage currentStage = (Stage) btn_items.getScene().getWindow();
            currentStage.close();
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String getUserFnByUsername(String username) {
        java.sql.Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:/C:\\Users\\aa\\SQLite\\sqlite-tools-win32-x86-3430200\\db_manip.db");
            String sql = "SELECT fn FROM users WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("fn");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                pstmt.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    String getUserPassByUsername(String username) {
        java.sql.Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:/C:\\Users\\aa\\SQLite\\sqlite-tools-win32-x86-3430200\\db_manip.db");
            String sql = "SELECT password FROM users WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                pstmt.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setFnTf(String fnTf) {
        tf_fullName.setText(fnTf);
    }

    public void setUnTf(String unTf) {
        tf_username.setText(unTf);
    }

    public void setPassTf(String passTf) {
        tf_password.setText(passTf);
    }

    private void openAccWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("account.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.setTitle("Personal Account");
            Stage currentStage = (Stage) btn_logout.getScene().getWindow();
            currentStage.close();
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
            Stage currentStage = (Stage) btn_logout.getScene().getWindow();
            currentStage.close();
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
