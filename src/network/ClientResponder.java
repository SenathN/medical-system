package network;

import mysqldb.DBUtil;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 *
 * @author Sen
 */
public class ClientResponder {

    private static String _RES_PATIENT = "_RES_PATIENT";
    private static String _NO_DATA = "_NO_DATA";

    public String handleRequest(String request) {
        String response = "";
        String[] segments = extractSegementsArray(request);

        try {
            switch (segments[0]) {
                case "GET" -> {
                    switch (segments[1]) {
                        case "PATIENT" -> {
                            response += _RES_PATIENT;
                            String patientData = getPatient(segments[2]);
                            
                            if (patientData.isEmpty() || patientData == null){
                                response += '\0'; // this character will cancel the response at the connection thread
                                break;
                            }
                            
                            response += '_' + patientData;
                        }
                        default -> {
                        }
                    }
                }

                default -> {
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Bad Request: " + e.getMessage());
        }

        if (response.isEmpty()) {
            response = _NO_DATA;
        }

        return response;
    }

    public static String getPatient(String req) {
        String query;
        switch (req) {
            case "LAST":
                query = "SELECT * FROM patients WHERE id = (SELECT MAX(id) FROM patients)";
                break;
            case "NEXT":
                query = "SELECT * FROM patients WHERE MAX(id)";
                break;
            case "PREVIOUS":
                query = "SELECT * FROM patients WHERE MIN(id)";
                break;
            default:
                query = "SELECT * FROM patients WHERE number=" + req;
        }

        ResultSet res = DBUtil.getResultSetFromQuery(query);
        String patientData = convertToString(res);

        return patientData;
    }

    //internal functions 
    private static String[] extractSegementsArray(String requesString) {
        int arrayLen = segmentCounter(requesString);
        String[] array = new String[arrayLen];
        int iter = 0;
        while (true) {
            String seg = extractSegment(requesString, iter);
            if (seg == null || seg.length() < 1) {
                break;
            }
            array[iter++] = seg;
        }

        return array;
    }

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
        }
        return "";
    }

    private static int segmentCounter(String requestString) {
        int counter = 0, index = 0;
        for (int i = 0; i < requestString.length(); i++) {
            if (requestString.charAt(index) == '_') {
                counter++;
            }
        }
        return counter;
    }

    private static String extractSegment(String requestString, int segmentIndex) {
        String response = "";
        int currentIndex = 0, currentSegment = -1;
        for (; currentIndex < requestString.length() && currentSegment <= segmentIndex; currentIndex++) {
            if (requestString.charAt(currentIndex) == '_') {
                currentSegment++;
                continue;
            }
            if (currentSegment == segmentIndex) {
                response += requestString.charAt(currentIndex);
                continue;
            }
        }
        return response;
    }
    
    public static void main(String[] args) {
        System.out.println(convertToString(DBUtil.getResultSetFromQuery("SELECT * FROM patients")));
    }
}
