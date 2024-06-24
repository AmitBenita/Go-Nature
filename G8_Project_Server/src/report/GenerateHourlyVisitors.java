package report;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GenerateHourlyVisitors implements Serializable {

    private String parkName;
    private String startDate;
    private String endDate;

    public GenerateHourlyVisitors(String parkName, String startDate, String endDate) {
        this.parkName = parkName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String generateHourlyVisitorCounts(Connection con) {
        try {
            String selectQuery = "SELECT date, entertime, exittime, numberofvisitors FROM enterdata WHERE parkname = ? AND date BETWEEN ? AND ?";
            try (PreparedStatement selectStatement = con.prepareStatement(selectQuery)) {
                selectStatement.setString(1, parkName);
                selectStatement.setString(2, startDate);
                selectStatement.setString(3, endDate);
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String date = resultSet.getString("date");
                        int numberOfVisitors = resultSet.getInt("numberofvisitors");
                        int hour = Integer.parseInt(resultSet.getString("entertime").split(":")[0]);
                        int visitTime = Integer.parseInt(resultSet.getString("exittime").split(":")[0]) - hour;
                        insertOrUpdateHourlyVisitorCount(con, date, hour, numberOfVisitors, visitTime, parkName);
                    }
                }
            }
            return "Success: Hourly visitor counts generated and updated.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    private void insertOrUpdateHourlyVisitorCount(Connection con, String date, int hour, int visitors, int visitTime, String parkName)
            throws SQLException {
        String hourColumn = "hour_" + String.format("%02d", hour);
        String groupColumn = visitors > 1 ? hourColumn + "_group_visitors" : "";
        String singleColumn = visitors == 1 ? hourColumn + "_single_visitors" : "";
        String avgColumn = visitors > 1 ? "daily_avg_group_visitors" : "daily_avg_single_visitors";

        // בדיקה האם הרשומה כבר קיימת בטבלה
        boolean exists = checkRecordExists(con, parkName, date);

        if (exists) {
            // עדכון רשומה קיימת
            if (!groupColumn.isEmpty() || !singleColumn.isEmpty()) {
                String updateVisitorColumn = !groupColumn.isEmpty() ? groupColumn : singleColumn;
                String updateQuery = "UPDATE dailyvisitorcount SET " + updateVisitorColumn + " = COALESCE(" + updateVisitorColumn + ", 0) + ? WHERE park_name = ? AND date = ?";
                try (PreparedStatement updateStmt = con.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, visitors);
                    updateStmt.setString(2, parkName);
                    updateStmt.setString(3, date);
                    updateStmt.executeUpdate();
                }
            }

            String updateAvgQuery = "UPDATE dailyvisitorcount SET " + avgColumn + " = (COALESCE(" + avgColumn + ", 0) + ?) / 2 WHERE park_name = ? AND date = ?";
            try (PreparedStatement updateAvgStmt = con.prepareStatement(updateAvgQuery)) {
                updateAvgStmt.setDouble(1, visitTime);
                updateAvgStmt.setString(2, parkName);
                updateAvgStmt.setString(3, date);
                updateAvgStmt.executeUpdate();
            }
        } else {
            String visitorColumn = visitors > 1 ? groupColumn : singleColumn;
            String insertQuery = "INSERT INTO dailyvisitorcount (park_name, date, " + visitorColumn + ", " + avgColumn + ") VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = con.prepareStatement(insertQuery)) {
                insertStmt.setString(1, parkName);
                insertStmt.setString(2, date);
                insertStmt.setInt(3, visitors);
                insertStmt.setDouble(4, visitTime); // זמן הביקור הראשון הוא הממוצע התחלתי
                insertStmt.executeUpdate();
            }
        }
    }

    private boolean checkRecordExists(Connection con, String parkName, String date) throws SQLException {
        String checkQuery = "SELECT COUNT(*) AS count FROM dailyvisitorcount WHERE park_name = ? AND date = ?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
            checkStmt.setString(1, parkName);
            checkStmt.setString(2, date);
            try (ResultSet resultSet = checkStmt.executeQuery()) {
                if (resultSet.next() && resultSet.getInt("count") > 0) {
                    return true;
                }
            }
        }
        return false;
    }


}
