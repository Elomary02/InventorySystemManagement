package com.example.gestioninventairemagasin;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String DATABASE_URL = "jdbc:sqlite:/C:\\Users\\aa\\SQLite\\sqlite-tools-win32-x86-3430200\\db_manip.db";

    public static Connection connect() {
        try {
            return (Connection) DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }
    }
}
