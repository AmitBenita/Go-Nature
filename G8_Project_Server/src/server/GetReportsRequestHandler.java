package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import report.GetReportsRequest;
import report.Report;

public class GetReportsRequestHandler {
	
	
	public static String handleRequest(GetReportsRequest request, Connection connection) {
        try {
            String query = "SELECT * FROM parkmanagerreports WHERE parkname = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, request.getParkName());
            ResultSet resultSet = statement.executeQuery();
            
            List<Report> reports = new ArrayList<>();
            while (resultSet.next()) {
                int reportId = resultSet.getInt("reportID");
                String parkName = resultSet.getString("parkname");
                String date = resultSet.getString("date");
                String type = resultSet.getString("type");
                String details = resultSet.getString("details"); 
                String Reportdate = resultSet.getString("ReportDate");                
                Report report = new Report(reportId, parkName, date, type+" for date: "+Reportdate, details);
                reports.add(report);
            }
            
            if (reports.isEmpty()) {
                return "Reports not found for park: " + request.getParkName();
            } else {
                return convertReportsToString(reports);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error fetching reports for park: " + request.getParkName();
        }
    }
    
    private static String convertReportsToString(List<Report> reports) {
    	StringBuilder responseBuilder = new StringBuilder();

    	for (Report report : reports) {
    	    responseBuilder.append(report.getReportId()).append(",")
    	            .append(report.getParkName()).append(",")
    	            .append(report.getDate()).append(",")
    	            .append(report.getType()).append(",")
    	            .append(report.getDetails()).append(",").append(";;");
    	}

    	String response = responseBuilder.toString();
    	if (response.endsWith(";;")) {
    	    response = response.substring(0, response.length() - 2);
    	}
    return response.toString();
}
}