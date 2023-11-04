module com.example.gestioninventairemagasin {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;


    requires org.controlsfx.controls;

    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires com.google.zxing;
    requires java.desktop;

    opens com.example.gestioninventairemagasin to javafx.fxml;
    exports com.example.gestioninventairemagasin;
}