package report;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Cancelereports  implements Serializable{

    private String parkName;
    private String startDate;
    private String endDate;

    public Cancelereports(String parkName, String startDate, String endDate) {
        this.parkName = parkName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String ordershandle(Connection con) throws SQLException {
        String query = "SELECT ordernumber, date, numberofvisitors, parkname, type FROM orders WHERE STR_TO_DATE(date, '%d/%m/%y') < CURDATE()";
        try (PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int orderNumber = rs.getInt("ordernumber");
                String date = rs.getString("date");
                int numOfVisitors = rs.getInt("numberofvisitors");
                String parkName = rs.getString("parkname");
                String type = rs.getString("type");

                String insertQuery = "INSERT INTO ordersdata (ordernumber, date, numofvisitors, parkname, type, status) VALUES (?, ?, ?, ?, ?, 'not happened')";
                
                try (PreparedStatement insertStmt = con.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, orderNumber);
                    insertStmt.setString(2, date);
                    insertStmt.setInt(3, numOfVisitors);
                    insertStmt.setString(4, parkName);
                    insertStmt.setString(5, type);

                    insertStmt.executeUpdate();
                }

                String deleteQuery = "DELETE FROM orders WHERE ordernumber = ?";
                
                try (PreparedStatement deleteStmt = con.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, orderNumber);
                    deleteStmt.executeUpdate();
                }
            }
        }

        String countQuery = "SELECT date, status, COUNT(*) as count FROM ordersdata WHERE parkname = ? AND date BETWEEN ? AND ? GROUP BY date, status";
        
        try (PreparedStatement countStmt = con.prepareStatement(countQuery)) {
            countStmt.setString(1, parkName);
            countStmt.setString(2, startDate);
            countStmt.setString(3, endDate);
            
            ResultSet countRs = countStmt.executeQuery();

            Map<String, Integer> canceledCounts = new HashMap<>();
            Map<String, Integer> notHappenedCounts = new HashMap<>();

            while (countRs.next()) {
                String date = countRs.getString("date");
                String status = countRs.getString("status");
                int count = countRs.getInt("count");

                if ("canceled".equals(status)) {
                    canceledCounts.put(date, count);
                } else if ("not happened".equals(status)) {
                    notHappenedCounts.put(date, count);
                }
            }

            String insertReportQuery = "INSERT INTO ordersreportsfordep (parkname, date, cancled, nothappend) VALUES (?, ?, ?, ?)";

            for (String date : canceledCounts.keySet()) {
                try (PreparedStatement insertReportStmt = con.prepareStatement(insertReportQuery)) {
                    insertReportStmt.setString(1, parkName);
                    insertReportStmt.setString(2, date);
                    insertReportStmt.setInt(3, canceledCounts.getOrDefault(date, 0));
                    insertReportStmt.setInt(4, notHappenedCounts.getOrDefault(date, 0));
                    insertReportStmt.executeUpdate();
                }
            }
        }
        return "";
    }



    public String getParkName() {
        return parkName;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    // Setters
    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}