package com.example.gestioninventairemagasin;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.ResourceBundle;

public class ProductsController implements Initializable {

    @FXML
    private Button btn_add;
    @FXML
    private Button btn_delete;
    @FXML
    private Button btn_update;
    @FXML
    private Button btn_home;
    @FXML
    private Button btn_items;
    @FXML
    private Button btn_logout;
    @FXML
    private Button btn_acc;
    @FXML
    private Button btn_gen_barcode;
    @FXML
    private TableView<Products> products_tableView;
    @FXML
    private ScrollBar scrollbar;
    @FXML
    private ImageView barcode_container;
    @FXML
    private TextField tf_productName;
    @FXML
    private TextField tf_quantity;
    @FXML
    private TextField tf_price;
    @FXML
    private TableColumn<Products, String> c1;
    @FXML
    private TableColumn<Products, Integer> c2;
    @FXML
    private TableColumn<Products, Double> c3;

    private ObservableList<Products> productsList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        products_tableView.setItems(productsList);
        btn_home.setOnAction(event -> openDashboardWindow());
        btn_items.setOnAction(event -> openItemsWindow());
        btn_acc.setOnAction(event -> openAccWindow());
        btn_logout.setOnAction(event -> openLoginWindow());
        btn_add.setOnAction(event -> addProduct());
        btn_update.setOnAction(event -> updateProduct());
        btn_delete.setOnAction(event -> deleteProduct());
        btn_gen_barcode.setOnAction(event -> generateBarcode());
        loadDataFromDatabase();
        products_tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tf_productName.setText(newValue.getProductName());
                tf_quantity.setText(Integer.toString(newValue.getQuantity()));
                tf_price.setText(Double.toString(newValue.getPrice()));
            }
        });
    }

    private int getLastInsertedProductId(java.sql.Connection conn) throws SQLException {
        int productId = -1;
        try (PreparedStatement stmt = conn.prepareStatement("SELECT last_insert_rowid()")) {
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                productId = resultSet.getInt(1);
            }
        }
        return productId;
    }

    private void addProduct() {
        String productName = tf_productName.getText();
        int quantity = Integer.parseInt(tf_quantity.getText());
        double price = Double.parseDouble(tf_price.getText());

        try (Connection conn = (Connection) Database.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO products (productName, productQuantity, productPrice) VALUES (?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, productName);
            stmt.setInt(2, quantity);
            stmt.setDouble(3, price);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating product failed, no rows affected.");
            }

            int productId = getLastInsertedProductId(conn);

            Products product = new Products(productId, productName, quantity, price);
            productsList.add(product); // Add the new product to the ObservableList
            c1.setCellValueFactory(new PropertyValueFactory<>("productName"));
            c2.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            c3.setCellValueFactory(new PropertyValueFactory<>("price"));
            products_tableView.refresh(); // Refresh the TableView

            int userId = UserData.getInstance().getId();
            insertProductLink(conn, userId, productId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Clear the text fields
        tf_productName.clear();
        tf_quantity.clear();
        tf_price.clear();
    }



    private void insertProductLink(java.sql.Connection conn, int userId, int productId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO link (user_id, product_id, action_timestamp) VALUES (?, ?, ?)")) {
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        }
    }


    private void updateProduct() {
        Products selectedProduct = products_tableView.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            String productName = tf_productName.getText();
            int quantity = Integer.parseInt(tf_quantity.getText());
            double price = Double.parseDouble(tf_price.getText());

            try (java.sql.Connection conn = (java.sql.Connection) Database.connect();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE products SET productName = ?, productQuantity = ?, productPrice = ? WHERE productId = ?")){

                stmt.setString(1, productName);
                stmt.setInt(2, quantity);
                stmt.setDouble(3, price);
                int userId = UserData.getInstance().getId();
                stmt.setInt(4, selectedProduct.getProdId());
                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Updating product failed, no rows affected.");
                }

                selectedProduct.setProductName(productName);
                selectedProduct.setQuantity(quantity);
                selectedProduct.setPrice(price);
                updateProductLink(conn, userId, selectedProduct.getProdId());
                products_tableView.refresh();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateProductLink(java.sql.Connection conn, int userId, int productId) throws SQLException {
        Products selectedProduct = products_tableView.getSelectionModel().getSelectedItem();
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE link SET user_id = ?, product_id = ?, action_timestamp = ? WHERE product_id")) {
            stmt.setInt(1, userId);
            stmt.setInt(2, selectedProduct.getProdId());
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        }
    }


    private void deleteProduct() {
        Products selectedProduct = products_tableView.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {

            try (java.sql.Connection conn = (java.sql.Connection) Database.connect();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM products WHERE productId = ?")) {
                stmt.setInt(1, selectedProduct.getProdId());

                 int affectedRows = stmt.executeUpdate();

                 if (affectedRows == 0) {
                    throw new SQLException("Deleting product failed, no rows affected.");
                 }
                int userId = UserData.getInstance().getId();

                deleteProductLink(conn, userId, selectedProduct.getProdId());

                productsList.remove(selectedProduct);
                products_tableView.refresh();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteProductLink(java.sql.Connection conn, int userId, int productId) throws SQLException {
        Products selectedProduct = products_tableView.getSelectionModel().getSelectedItem();
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM link WHERE product_id = ?")) {
            stmt.setInt(1, userId);
            stmt.setInt(1, selectedProduct.getProdId());
            stmt.executeUpdate();
        }
    }


    private void generateBarcode() {
        try {
            String text = "Product: " + tf_productName.getText()+ "\nQuantity in stock: " + tf_quantity.getText()+ "\nPrice: " + tf_price.getText();
            int width = 200;
            int height = 200;

            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] byteArray = baos.toByteArray();

            javafx.scene.image.Image qrImage = new javafx.scene.image.Image(new ByteArrayInputStream(byteArray));
            barcode_container.setImage(qrImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void loadDataFromDatabase() {
        productsList.clear(); // Clear the existing data

        try (java.sql.Connection conn = (java.sql.Connection) Database.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM products")) {
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                int productId = resultSet.getInt("productId");
                String productName = resultSet.getString("productName");
                int quantity = resultSet.getInt("productQuantity");
                double price = resultSet.getDouble("productPrice");

                Products product = new Products(productId, productName, quantity, price);
                productsList.add(product); // Add the product to the ObservableList
            }

            products_tableView.setItems(productsList); // Set the items of the TableView to the loaded data
            c1.setCellValueFactory(new PropertyValueFactory<>("productName"));
            c2.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            c3.setCellValueFactory(new PropertyValueFactory<>("price"));
        } catch (SQLException e) {
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
            loadDataFromDatabase();
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
