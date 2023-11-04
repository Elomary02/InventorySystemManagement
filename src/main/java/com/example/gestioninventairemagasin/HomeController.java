package com.example.gestioninventairemagasin;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class HomeController implements Initializable {


    @FXML
    private Button btn_dashb;
    @FXML
    private Button btn_items;
    @FXML
    private Button btn_acc;
    @FXML
    private Button btn_logout;
    @FXML
    private Label label_welcome;
    @FXML
    private Label label_date;
    @FXML
    private Label label_soldToday;
    @FXML
    private Label label_outOfStock;
    @FXML
    private Label label_TotallCells;

    private String welcomeText;
    private Stage primaryStage;

    public HomeController() {

    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setWelcomeText("Welcome " + UserData.getInstance().getUsername() + "!");
        setLabel_date(String.valueOf(LocalDate.now()));
        btn_dashb.setOnAction(event -> openDashboardWindow());
        btn_items.setOnAction(event -> openItemsWindow());
        btn_acc.setOnAction(event -> openAccWindow());
        btn_logout.setOnAction(event -> openLoginWindow());
        updateLabelSoldToday();
        updateLabelOutOfStock();
        updateLabelTotalSales();

    }

    public void updateLabelSoldToday() {
        int userId = getUserIdByUsername(UserData.getInstance().getUsername());
        if (userId != -1) {
            double totalSales = calculateTotalSales(userId);
            setLabel_soldToday(String.valueOf(totalSales));
        } else {
            setLabel_soldToday("No Sales Yet!");
        }
    }

    private int getUserIdByUsername(String username) {
        java.sql.Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:/C:\\Users\\aa\\SQLite\\sqlite-tools-win32-x86-3430200\\db_manip.db");
            String sql = "SELECT id FROM users WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
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
        return -1;
    }

    private double calculateTotalSales(int userId) {
        double totalSales = 0;
        java.sql.Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:/C:\\Users\\aa\\SQLite\\sqlite-tools-win32-x86-3430200\\db_manip.db");
            String sql = "SELECT SUM(productPrice) AS total FROM link "
                    + "INNER JOIN products ON link.product_id = products.productId "
                    + "WHERE link.user_id = ? "
                    + "AND link.action_timestamp >= ?";

            LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setTimestamp(2, Timestamp.valueOf(twentyFourHoursAgo));
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                totalSales = resultSet.getDouble("total");
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
        return totalSales;
    }

    public void updateLabelOutOfStock() {
        StringBuilder outOfStockProducts = new StringBuilder();
        java.sql.Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:/C:\\Users\\aa\\SQLite\\sqlite-tools-win32-x86-3430200\\db_manip.db");
            String sql = "SELECT productId FROM products WHERE productQuantity < 2";
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int productId = resultSet.getInt("productId");
                outOfStockProducts.append(productId).append(", ");
            }

            if (outOfStockProducts.length() > 0) {
                outOfStockProducts.setLength(outOfStockProducts.length() - 2);
                setLabel_outOfStock(String.valueOf(outOfStockProducts));
            } else {
                setLabel_outOfStock("No Product.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
                preparedStatement.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void updateLabelTotalSales() {
        int userId = UserData.getInstance().getId();
        if (userId != -1) {
            double totalSales = calculateTotalSales(userId);
            setLabel_TotallCells(String.valueOf(totalSales));
        } else {
            setLabel_TotallCells("Total Sales: N/A");
        }
    }

    public void setWelcomeText(String welcomeText) {
        label_welcome.setText(welcomeText);
    }

    public void setLabel_date(String labelDate) {
        label_date.setText(labelDate);
    }

    public void setLabel_soldToday(String labelSoldToday) {
        label_soldToday.setText(labelSoldToday);
    }

    public void setLabel_outOfStock(String labelOutOfStock) {
        label_outOfStock.setText(labelOutOfStock);
    }

    public void setLabel_TotallCells(String labelTotallCells) {
        label_TotallCells.setText(labelTotallCells);
    }
    private void openDashboardWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.setTitle("Dashboard");
            Stage currentStage = (Stage) btn_dashb.getScene().getWindow();
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

    private void openAccWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("account.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.setTitle("Personal Account");
            AccountController setFn = loader.getController();
            setFn.getUserFnByUsername(UserData.getInstance().getUsername());
            AccountController setUn = loader.getController();
            setUn.setUnTf(UserData.getInstance().getUsername());
            AccountController setPass = loader.getController();
            setPass.getUserPassByUsername(UserData.getInstance().getUsername());
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
