package server;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangeParkSetting  implements Serializable {
    private String maxVisitors;
    private String maxOrders;
    private String averageVisit;
    //private String CurrentVisitors;
    private String ParkName;
    private String WhatToDo;
    private String Details;

    public ChangeParkSetting(String ParkName) {
        this.ParkName = ParkName;
    }

    public String GetAndChangeSetting(Connection Con) {
        try {
            if (WhatToDo.equals("Show")) {
                String query = "SELECT MaxVisitors, MaxOrders, AverageVisit FROM parks WHERE ParkName = ?";
                PreparedStatement pstmt = Con.prepareStatement(query);
                pstmt.setString(1, ParkName);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    maxVisitors = rs.getString("MaxVisitors");
                    maxOrders = rs.getString("MaxOrders");
                    averageVisit = rs.getString("AverageVisit");
                    //CurrentVisitors = rs.getString("CurrentVisitors");
                    Details=maxVisitors+" "+maxOrders+" "+averageVisit;
                } else {
                    System.out.println("No data found for park: " + ParkName);
                }

                rs.close();
                pstmt.close();
                return Details;
            }
            else if (WhatToDo.equals("Update")) {
                try {
                    // Check if a row with type=ChangeSetting and parkname=ParkName already exists
                    String checkQuery = "SELECT * FROM requests WHERE parkname=?";
                    PreparedStatement checkStmt = Con.prepareStatement(checkQuery);
                    checkStmt.setString(1, ParkName);
                    ResultSet resultSet = checkStmt.executeQuery();

                    if (resultSet.next()) {
                        // Update the existing row
                        String updateQuery = "UPDATE requests SET MaxCap=?, MaxOrders=?, AvarageVisit=? WHERE parkname=?";
                        PreparedStatement updateStmt = Con.prepareStatement(updateQuery);
                        updateStmt.setString(1, maxVisitors);
                        updateStmt.setString(2, maxOrders);
                        updateStmt.setString(3, averageVisit);
                        updateStmt.setString(4, ParkName);
                        ////need to add here?
                        updateStmt.executeUpdate();
                        updateStmt.close();
                    } else {
                    	// Insert the new data into requests table
                        StringBuilder updateQuery = new StringBuilder("INSERT INTO requests (parkname,MaxCap,MaxOrders,AvarageVisit) VALUES ( ?, ?, ?, ?)");
                        PreparedStatement pstmt = Con.prepareStatement(updateQuery.toString());

                        pstmt.setString(1, ParkName);
                        pstmt.setString(2, maxVisitors);
                        pstmt.setString(3, maxOrders);
                        pstmt.setString(4, averageVisit);

                        pstmt.executeUpdate();
                        pstmt.close();
                        }

                    
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("No action specified.");
            }

            	             	
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Details="done";
        return Details;
    }

    // Getters and setters
    public String getMaxVisitors() {
        return maxVisitors;
    }

    public void setMaxVisitors(String maxVisitors) {
        this.maxVisitors = maxVisitors;
    }

    public String getMaxOrders() {
        return maxOrders;
    }

    public void setMaxOrders(String maxOrders) {
        this.maxOrders = maxOrders;
    }

    public String getAverageVisit() {
        return averageVisit;
    }

    public void setAverageVisit(String averageVisit) {
        this.averageVisit = averageVisit;
    }

    public String getParkName() {
        return ParkName;
    }

    public void setParkName(String ParkName) {
        this.ParkName = ParkName;
    }

    public String getWhatToDo() {
        return WhatToDo;
    }

    public void setWhatToDo(String WhatToDo) {
        this.WhatToDo = WhatToDo;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String Details) {
        this.Details = Details;
    }
}
