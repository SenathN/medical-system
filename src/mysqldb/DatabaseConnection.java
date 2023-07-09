/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mysqldb;

import java.sql.*;
import javax.swing.JOptionPane;
import jframes.MainInterface;

/**
 *
 * @author Sen
 */
public class DatabaseConnection {

    public static Connection conn = null;
    private static String dbFile = "C:/sqlite/medicalSystem.db";

    public static Connection getConnection() {
        if (conn == null) {
            synchronized (DatabaseConnection.class) {
                if (conn == null) {
                    MainInterface ref = MainInterface.getJFrame();
                    try {
                        Class.forName("org.sqlite.JDBC");
                        conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile);

                        //checking if needed databases are created
                        tableCheck();

                        ref.SQLDatabaseConnection(true);
                        return conn;
                    } catch (Exception e) {
                        System.out.println("Connection failed : " + e);
                        String errorMessage = "An error occurred: " + e.getMessage();
                        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                        ref.SQLDatabaseConnection(false);
                        return null;
                    }
                }
            }
        }
        return conn;
    }

    private static void tableCheck() {
        System.out.println("Database check started..");

        String createPatientsTableIfNotExistsSQL
                = "CREATE TABLE IF NOT EXISTS patients (\n"
                + "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "  number INTEGER,\n"
                + "  name VARCHAR(50),\n"
                + "  type VARCHAR(50),\n"
                + "  dob DATE,\n"
                + "  prescriptionId INTEGER UNIQUE,\n"
                + "  appointmentDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n"
                + ");";

        try {
            conn.createStatement().executeUpdate(createPatientsTableIfNotExistsSQL);
            System.out.println("patients table ready");

            System.out.println("Database check success");
        } catch (Exception e) {
            String errorMessage = "An error occurred: " + e.getMessage();
            JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
