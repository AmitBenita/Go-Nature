package report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.Serializable;

public class RetrieveOrdersReports implements Serializable {
    private String parkName;
    private String startDate;
    private String endDate;

    public RetrieveOrdersReports(String parkName, String startDate, String endDate) {
        this.parkName = parkName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String ReportsfromDB(Connection con) throws SQLException {
        StringBuilder report = new StringBuilder();

        String query = "SELECT parkname, date, cancled, nothappend FROM ordersreportsfordep WHERE parkname = ? AND STR_TO_DATE(date, '%d/%m/%y') BETWEEN STR_TO_DATE(?, '%d/%m/%y') AND STR_TO_DATE(?, '%d/%m/%y')";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, parkName);
            stmt.setString(2, startDate);
            stmt.setString(3, endDate);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String parkName = rs.getString("parkname");
                    String date = rs.getString("date");
                    int canceled = rs.getInt("cancled");
                    int nothappend = rs.getInt("nothappend");

                    report.append("parkname: ").append(parkName).append(", date: ").append(date)
                            .append(", canceled: ").append(canceled).append(", nothappend: ").append(nothappend).append(", ");
                }
            }
        }

        if (report.length() > 2) {
            report.delete(report.length() - 2, report.length());
        }

        return report.toString();
    }

    // Getters and Setters ...
}
