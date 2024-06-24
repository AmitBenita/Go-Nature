package report;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RetrieveHourlyVisitors implements Serializable {
    String parkName;
    String startDate;
    String endDate;

    public RetrieveHourlyVisitors(String parkName, String startDate, String endDate) {
        this.parkName = parkName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public ArrayList<String> retrieveHourlyVisitorCounts(Connection con) {
        ArrayList<String> dataRows = new ArrayList<>();
        try {
            String query = "SELECT * FROM dailyvisitorcount WHERE park_name = ? AND date BETWEEN ? AND ?";
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, parkName);
                stmt.setString(2, startDate);
                stmt.setString(3, endDate);
                try (ResultSet resultSet = stmt.executeQuery()) {
                    while (resultSet.next()) {
                        StringBuilder row = new StringBuilder();
                        row.append("park_name:").append(resultSet.getString("park_name")).append(", ");
                        row.append("date:").append(resultSet.getString("date")).append(", ");
                        for (int hour = 8; hour <= 17; hour++) {
                            String hourStr = String.format("%02d", hour);
                            row.append("hour_").append(hourStr).append("_group_visitors:").append(resultSet.getInt("hour_" + hourStr + "_group_visitors")).append(", ");
                            row.append("hour_").append(hourStr).append("_single_visitors:").append(resultSet.getInt("hour_" + hourStr + "_single_visitors")).append(", ");
                        }
                        row.append("daily_avg_group_visitors:").append(resultSet.getDouble("daily_avg_group_visitors")).append(", ");
                        row.append("daily_avg_single_visitors:").append(resultSet.getDouble("daily_avg_single_visitors"));
                        dataRows.add(row.toString());
                    }
                }
            }
            return dataRows;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String retrieveHourlyVisitorCountsAsString(Connection con) {
        ArrayList<String> dataRows = retrieveHourlyVisitorCounts(con);
        if (dataRows == null) {
            return "Error retrieving data";
        }
        
        return String.join(", ", dataRows);
    }
}
