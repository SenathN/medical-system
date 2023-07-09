/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mysqldb;

import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Sen
 */
public class DBUtil {

    public static ResultSet getResultSetFromQuery(String query) {
        Connection conn = DatabaseConnection.getConnection();
        ResultSet res = null;

        try {
            Statement st = conn.createStatement();
            res = st.executeQuery(query);

        } catch (Exception e) {
            System.out.println("Exception : " + e);
            String errorMessage = "An error occurred: " + e.getMessage();
            JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return res;
    }

    public static DefaultTableModel getDefaultTableModelFromQuery(String query) {
        DefaultTableModel model;

        try {
            ResultSet tableData = getResultSetFromQuery(query);
            ResultSetMetaData resultDat = tableData.getMetaData();
            String[] columnNames = new String[resultDat.getColumnCount()];

            for (int i = 1; i <= resultDat.getColumnCount(); i++) {
                columnNames[i - 1] = resultDat.getColumnName(i);
            }
            model = new DefaultTableModel(columnNames, 0);

            while (tableData.next()) {
                model.addRow(new Object[]{
                    tableData.getInt(1),
                    tableData.getInt(2),
                    tableData.getString(3),
                    tableData.getString(4),
                    tableData.getString(5),
                    tableData.getInt(6),
                    tableData.getString(7)
                });
            }
            return model;
        } catch (Exception e) {
            System.out.println("SQL Exception occured : " + e.getLocalizedMessage());
            String errorMessage = "An error occurred: " + e.getMessage();
            JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    // patient id and number are different, only use patient number for day today usage
    public static String createPatientEntry(int number, String name, String type, String dob) {
        Connection conn = DatabaseConnection.getConnection();
        String query = "INSERT into patients (number, name, type, dob, prescriptionId) values (?, ?, ?, ?, ?)";

        number = Integer.parseInt(String.valueOf(number)) > 0 ? number : 1;
        name = name.length() > 0 ? name : "patient " + number;
        try (PreparedStatement ps = conn.prepareStatement(query)) {
//            ps.setInt(1, id);
            ps.setInt(1, number);
            ps.setString(2, name);
            ps.setString(3, type);
            ps.setString(4, dob);
            ps.setInt(5, (int) (Math.random() * 10000));

            int afffected = ps.executeUpdate();
            if (afffected != 0) {
                return "Insert success";
            }

        } catch (Exception e) {
            System.out.println(e);
            String errorMessage = "An error occurred: " + e.getMessage();
            JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
        }
        return "Insert failed";
    }

    public static int executeManipulationQuery(String query) {
        Connection conn = DatabaseConnection.getConnection();

        try (Statement ps = conn.createStatement()) {
            int afffected = ps.executeUpdate(query);
            if (afffected != 0) {
                return afffected;
            }

        } catch (Exception e) {
            System.out.println(e);
            String errorMessage = "An error occurred: " + e.getMessage();
            JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }

    //  for development purposes
    public static void main(String[] args) {
//        executeManipulationQuery("Drop table patients");
        getNextPatientNumber();
    }

    /*
    patient number starts from 0 and goes onward until the end of the day, at which it resets back to 1
     */
    public static int getNextPatientNumber() {
        String value = convertToString(getResultSetFromQuery(
                "SELECT MAX(number) "
                + "FROM patients "
                + "WHERE DATE(appointmentDate) = CURRENT_DATE"));
        try {
            return Integer.parseInt(value.strip()) + 1;
        } catch (NumberFormatException e) {
            System.out.println("value " + value);
            return 1;
        }
    }

    // internal functions
    private static String convertToString(ResultSet resultSet) {
        try {
            StringBuilder sb = new StringBuilder();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Iterate over the rows of the result set
            while (resultSet.next()) {
                // Iterate over the columns of each row
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) {
                        sb.append(", "); // Separator between columns
                    }
                    var value = resultSet.getString(i);
                    sb.append(value);
                }
                sb.append(System.lineSeparator()); // New line after each row
            }

            return sb.toString();
        } catch (SQLException ex) {
            System.out.println("DB error : " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return "";
    }
}
