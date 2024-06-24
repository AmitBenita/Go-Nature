
package server;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import report.ReportEntry;
import report.ReportNumberOfVisitorsRequest;
//import report.ReportNumberOfVisitorsResponse;
import report.UsageReportEntry;
import report.UsageReportRequest;
//import report.UsageReportResponse;

/**
 * The ParkManagementServer class is responsible for handling server-side logic related to park management
 * and report generation. It processes requests for generating various reports about park visitors and usage,
 * interacting with a database to fetch and aggregate relevant data.
 */
public class ParkManagementServer implements Serializable{

    private String Result = "";
    private ReportNumberOfVisitorsRequest requestNumOfVisitors;
    private UsageReportRequest requestUsage;
    private String reportType;
    
    public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public ReportNumberOfVisitorsRequest getRequestNumOfVisitors() {
		return requestNumOfVisitors;
	}

	public void setRequestNumOfVisitors(ReportNumberOfVisitorsRequest requestNumOfVisitors) {
		this.requestNumOfVisitors = requestNumOfVisitors;
	}

	public UsageReportRequest getRequestUsage() {
		return requestUsage;
	}

	public void setRequestUsage(UsageReportRequest requestUsage) {
		this.requestUsage = requestUsage;
	}

	public ParkManagementServer() {
		super(); // Calls the parent class' constructor. Necessary for serializable classes
	}


    /**
     * Handles the request for generating the number of visitors report for a specific park and date range.
     *
     * @param request the request object containing the date range and park name
     * @param con the database connection
     * @return a string representing the report
     */
    public String handleReportNumberOfVisitorsRequest(Connection con) {
        LocalDate startDate = requestNumOfVisitors.getFromDate();
        LocalDate endDate = requestNumOfVisitors.getToDate();
        String DateRange= requestNumOfVisitors.getFromDate() +" - "+ requestNumOfVisitors.getToDate();
        String parkName = requestNumOfVisitors.getParkName();
        // SQL query to count visitors by type within the specified date range for a given park
        String query = "SELECT e.type AS VisitorType, COUNT(e.type) AS NumberOfRecords, SUM(CAST(e.numberofvisitors AS SIGNED)) AS TotalVisitors " +
        "FROM enterdata e " +
        "JOIN parks p ON e.parkname = p.parkname " +
        "WHERE STR_TO_DATE(e.date, '%d/%m/%y') BETWEEN ? AND ? AND p.parkname = ? " +
        "GROUP BY e.type";
        
        StringBuilder resultBuilder = new StringBuilder(parkName);
        resultBuilder.append(": ");

        System.out.println("Executing query: " + query);
        System.out.println("Query parameters: startDate=" + startDate + ", endDate=" + endDate + ", parkName=" + parkName);

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            // Setting query parameters
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            stmt.setString(3, parkName);
            ResultSet rs = stmt.executeQuery();

            // Check if we have results, if not, return "No data" message
            if (!rs.isBeforeFirst()) {
                return parkName + ": No data";
            }

            while (rs.next()) {
                String visitorType = rs.getString("VisitorType");
                int numberOfRecords = rs.getInt("NumberOfRecords");
                int totalVisitors = rs.getInt("TotalVisitors");

                if (visitorType.equals("notregister")) {
                    resultBuilder.append(totalVisitors).append(" Unregistered, ");
                } else if (visitorType.equals("Grouporder")) {
                    resultBuilder.append(totalVisitors).append(" Group, ");
                } else if (visitorType.equals("Personalorder")) {
                    resultBuilder.append(totalVisitors).append(" Registered, ");
                }else if (visitorType.equals("Guideorder")) {
                    resultBuilder.append(totalVisitors).append(" GuideRegistered, ");
                }
            }

            // Remove the last comma and space
            resultBuilder.setLength(resultBuilder.length() - 2);

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error generating report";
        }
        
        ////start construction of an adapted string for the View Report screen
        String originalResult = resultBuilder.toString();

     // Removes the colon and the name at the beginning
     String withoutParkName = originalResult.substring(originalResult.indexOf(":") + 2);

     // Changing the format structure
     String newResult = withoutParkName
         .replace(" Unregistered", ",Unregistered")
         .replace(" Group", ",Group")
         .replace(" Registered", ",Registered")
         .replace(" GuideRegistered", ",GuideRegistered")
         .replaceAll(", ", ","); 

     // Removing the first comma, if necessary
     if (newResult.startsWith(",")) {
         newResult = newResult.substring(1);
     }

     // Sending to database to table parkmanagerreports
     System.out.println(newResult);
     insertReportToDatabase(con, "ReportNumberOfVisitors", newResult, requestNumOfVisitors.getParkName(),DateRange);
     ////end construction
        
        return resultBuilder.toString();//send the original string
    }
    
    

	////parkmanagerreports (reportID ,reportType, details, createdBy, parkName, createDate)

    /**
     * Generates a usage report for a specific park and date, detailing the percentage occupancy by hour.
     * This method queries the database to calculate the occupancy percentage for each hour of the park's operating hours
     * and formats the results into a string representation of the report.
     *
     * @param con The database connection to use for the query.
     * @return A string representation of the usage report.
     */
    public String handleUsageReportRequest(Connection con) {
        LocalDate selectedDate = requestUsage.getDate();
        String parkName = requestUsage.getParkName();

        String query = "SELECT HOUR(TIME(CONCAT(hourTable.hour, ':00:00'))) AS Hour, " +
                "       ROUND(IFNULL((SELECT SUM(enterdata.numberofvisitors) " +
                "                      FROM enterdata " +
                "                      WHERE STR_TO_DATE(enterdata.date, '%d/%m/%y') = STR_TO_DATE(?, '%d/%m/%y') " +
                "                        AND enterdata.parkname = ? " +
                "                        AND ((enterdata.exittime IS NOT NULL " +
                "                              AND TIME(enterdata.entertime) <= TIME(CONCAT(hourTable.hour, ':00:00')) " +
                "                              AND TIME(enterdata.exittime) > TIME(CONCAT(hourTable.hour, ':00:00'))) " +
                "                             OR (enterdata.exittime IS NULL " +
                "                                 AND TIME(enterdata.entertime) <= TIME(CONCAT(hourTable.hour, ':00:00')) " +
                "                                 AND TIME(ADDTIME(enterdata.entertime, SEC_TO_TIME(CAST(parks.averagevisit AS UNSIGNED) * 60))) > TIME(CONCAT(hourTable.hour, ':00:00'))))), 0) * 100.0 / CAST(parks.maxvisitors AS UNSIGNED), 2) AS OccupancyPercentage " +
                "FROM ( " +
                "    SELECT '08' AS hour UNION ALL " +
                "    SELECT '09' UNION ALL " +
                "    SELECT '10' UNION ALL " +
                "    SELECT '11' UNION ALL " +
                "    SELECT '12' UNION ALL " +
                "    SELECT '13' UNION ALL " +
                "    SELECT '14' UNION ALL " +
                "    SELECT '15' UNION ALL " +
                "    SELECT '16' UNION ALL " +
                "    SELECT '17' " +
                ") AS hourTable " +
                "JOIN parks ON parks.parkname = ? " +
                "GROUP BY hourTable.hour " +
                "ORDER BY hourTable.hour";
        
//        String query = "SELECT HOUR(TIME(CONCAT(hourTable.hour, ':00:00'))) AS Hour, " +
//                "       ROUND(IFNULL(SUM(enterdata.numberofvisitors), 0) * 100.0 / parks.maxvisitors, 2) AS OccupancyPercentage " +
//                "FROM ( " +
//                "    SELECT '08' AS hour UNION ALL " +
//                "    SELECT '09' UNION ALL " +
//                "    SELECT '10' UNION ALL " +
//                "    SELECT '11' UNION ALL " +
//                "    SELECT '12' UNION ALL " +
//                "    SELECT '13' UNION ALL " +
//                "    SELECT '14' UNION ALL " +
//                "    SELECT '15' UNION ALL " +
//                "    SELECT '16' UNION ALL " +
//                "    SELECT '17' " +
//                ") AS hourTable " +
//                "LEFT JOIN enterdata ON STR_TO_DATE(enterdata.date, '%d/%m/%y') = STR_TO_DATE(?, '%d/%m/%y') " +
//                "                   AND enterdata.parkname = ? " +
//                "                   AND HOUR(enterdata.entertime) = HOUR(TIME(CONCAT(hourTable.hour, ':00:00'))) " +
//                "JOIN parks ON enterdata.parkname = parks.parkname " +
//                "GROUP BY hourTable.hour " +
//                "ORDER BY hourTable.hour";

        System.out.println("Executing query: " + query);
        System.out.println("Query parameters: selectedDate=" + selectedDate + ", parkName=" + parkName);

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, selectedDate.format(DateTimeFormatter.ofPattern("d/M/yy")));
            stmt.setString(2, parkName);
            stmt.setString(3, parkName);

            ResultSet rs = stmt.executeQuery();

            StringBuilder reportBuilder = new StringBuilder();

            Map<Integer, Double> occupancyMap = new HashMap<>();

            for (int hour = 8; hour <= 17; hour++) {
                occupancyMap.put(hour, 0.0);
            }

            while (rs.next()) {
                int hour = rs.getInt("Hour");
                double occupancyPercentage = rs.getDouble("OccupancyPercentage");
                occupancyMap.put(hour, occupancyPercentage);
            }

            for (int hour = 8; hour <= 17; hour++) {
                reportBuilder.append(String.format("%02d:00", hour)).append(",").append(occupancyMap.get(hour)).append(";");
            }

            if (reportBuilder.length() > 0) {
                reportBuilder.setLength(reportBuilder.length() - 1);
            }
            
            //put parkName in start of the return string if need-DONT NEED NOW!*
            //example: crazypark,08:00,0.0...0.0;15:00,0.0;16:00,0.0;17:00,0.0
            //StringBuilder reportBuilderStartNamePark = new StringBuilder(parkName + ",");
            //reportBuilderStartNamePark.append(reportBuilder.toString());
            //return reportBuilderStartNamePark.toString();**
            
            //lines correct - return string without parkName in start
            //example: 08:00,0.0...0.0;15:00,0.0;16:00,0.0;17:00,0.0
            insertReportToDatabase(con, "UsageReport", reportBuilder.toString(), requestUsage.getParkName(),selectedDate.format(DateTimeFormatter.ofPattern("d/M/yy")));
            return reportBuilder.toString();

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error generating report";
        }
    }
    
    /**
     * Inserts a generated report into the database.
     * This method is called after generating a report to save the report details in the database for future reference.
     *
     * @param con        The database connection to use for inserting the report.
     * @param reportType The type of report being inserted.
     * @param details    The detailed contents of the report.
     * @param parkName   The name of the park to which the report relates.
     * @return The ID of the inserted report as generated by the database.
     */
    public int insertReportToDatabase(Connection con, String reportType, String details, String parkName,String ReportDate) {
    	String query = "INSERT INTO parkmanagerreports (parkname, date, type, details,ReportDate) VALUES (?, ?, ?, ?,?)";
        int reportId = 0;
        
        try (PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        	stmt.setString(1, parkName);
            stmt.setDate(2, Date.valueOf(LocalDate.now()));
            stmt.setString(3, reportType);
            stmt.setString(4, details);
            stmt.setString(5, ReportDate);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating report failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reportId = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reportId;
}
}


/////////////////////
////    	return "08:00,10.0,09:00,20.00,10:00,5.0;11:00,10.0;12:00,70.0;13:00,80.0;14:00,20.0;15:00,10.0,16:00,13.00,17:00,3.00";
//
//CREATE TABLE Parks_report2 (
//
//ParkID INT PRIMARY KEY,
//
//ParkName VARCHAR(100),
//
//MaxVisitors INT
//
//);
//
//-- Insert sample data into Parks table
//
//INSERT INTO Parks_report2 (ParkID, ParkName, MaxVisitors)
//
//VALUES
//
//(1, 'crazypark', 100),
//
//(2, 'simplepark', 150),
//
//(3, 'nicepark', 200);
//
//-- Create Orders table
//
//CREATE TABLE Orders_report2 (
//
//OrderID INT PRIMARY KEY,
//
//VisitorType VARCHAR(20),
//
//NumVisitors INT,
//
//VisitDate DATE,
//
//ParkID INT,
//
//FOREIGN KEY (ParkID) REFERENCES Parks_report2(ParkID)
//
//);
//
//-- Insert sample data into Orders table
//
//INSERT INTO Orders_report2 (OrderID, VisitorType, NumVisitors, VisitDate, ParkID)
//
//VALUES
//
//-- Crazypark orders
//
//(1, 'Individual', 5, '2023-06-01', 1),
//
//(2, 'Group', 20, '2023-06-01', 1),
//
//(3, 'Member', 10, '2023-06-01', 1),
//
//(4, 'Individual', 8, '2023-06-02', 1),
//
//(5, 'Group', 25, '2023-06-02', 1),
//
//(6, 'Member', 15, '2023-06-02', 1),
//
//-- Add more orders for crazypark throughout the month
//
//-- ...
//
//-- Simplepark and Nicepark orders
//
//(101, 'Individual', 3, '2023-06-04', 2),
//
//(102, 'Group', 12, '2023-06-05', 2),
//
//(103, 'Member', 6, '2023-06-06', 2),
//
//(104, 'Individual', 2, '2023-06-07', 3),
//
//(105, 'Group', 15, '2023-06-08', 3),
//
//(106, 'Member', 4, '2023-06-09', 3),
//
//-- Add more orders for simplepark and nicepark
//
//-- ...
//
//(200, 'Individual', 4, '2023-06-30', 1),
//
//(201, 'Group', 18, '2023-06-30', 1),
//
//(202, 'Member', 8, '2023-06-30', 1);
//
//-- Create Visits table
//
//CREATE TABLE Visits_report2 (
//
//VisitID INT PRIMARY KEY,
//
//OrderID INT,
//
//VisitDate DATE,
//
//EntryTime TIME,
//
//ExitTime TIME,
//
//FOREIGN KEY (OrderID) REFERENCES Orders_report2(OrderID)
//
//);
//
//-- Insert sample data into Visits table
//
//INSERT INTO Visits_report2 (VisitID, OrderID, VisitDate, EntryTime, ExitTime)
//
//VALUES
//
//-- Crazypark visits
//
//(1, 1, '2023-06-01', '10:00:00', '12:30:00'),
//
//(2, 2, '2023-06-01', '09:30:00', '14:00:00'),
//
//(3, 3, '2023-06-01', '11:00:00', '15:30:00'),
//
//(4, 4, '2023-06-02', '10:30:00', '13:00:00'),
//
//(5, 5, '2023-06-02', '09:00:00', '12:30:00'),
//
//(6, 6, '2023-06-02', '11:30:00', '16:00:00'),
//
//-- Add more visits for crazypark throughout the month
//
//-- ...
//
//-- Simplepark and Nicepark visits
//
//(101, 101, '2023-06-04', '10:00:00', '12:00:00'),
//
//(102, 102, '2023-06-05', '09:30:00', '14:30:00'),
//
//(103, 103, '2023-06-06', '11:00:00', '14:00:00'),
//
//(104, 104, '2023-06-07', '10:30:00', '13:30:00'),
//
//(105, 105, '2023-06-08', '09:00:00', '13:00:00'),
//
//(106, 106, '2023-06-09', '11:30:00', '15:30:00'),
//
//-- Add more visits for simplepark and nicepark
//
//-- ...
//
//(200, 200, '2023-06-30', '10:00:00', '12:00:00'),
//
//(201, 201, '2023-06-30', '09:30:00', '15:00:00'),
//
//(202, 202, '2023-06-30', '11:00:00', '16:00:00');








