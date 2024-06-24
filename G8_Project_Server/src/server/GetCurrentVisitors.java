package server;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetCurrentVisitors  implements Serializable {
    private String CurrentVisitors;//current visitors in the park
    private String ParkName;
    private String Details;//string for send back
    private String maxVisitors; 
    private String WhatToDo;


    public GetCurrentVisitors(String ParkName) {
        this.ParkName = ParkName;
    }

    public String getCurrentVisitors(Connection Con) {
    	if (WhatToDo.equals("Current")) {
    	String query = "SELECT currentvisitors FROM livedata WHERE parkname = ?";
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	String CurrentVisitors = ""; 
    	try {
    	    pstmt = Con.prepareStatement(query);
    	    pstmt.setString(1, ParkName);
    	    rs = pstmt.executeQuery();

    	    if (rs.next()) {
    	        CurrentVisitors = rs.getString("currentvisitors");
    	        Details = CurrentVisitors;
    	    } else {
    	        System.out.println("No data found for park: " + ParkName);
    	        Details = "No data found";
    	    }
    	} catch (SQLException e) {
    	    e.printStackTrace();
    	    Details = "Error";}
//    	} finally {
//    	    if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
//    	    if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
//    	}
    	return Details;
    }
    	Details="getCurrentVisitors failed";
        return Details;
    }
    
    	 public String getMaxVisitors(Connection Con) {
    		 if (WhatToDo.equals("Max")) {
        String query = "SELECT MaxVisitors FROM parks WHERE ParkName = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
    	String maxVisitors = ""; 
    	try {
    	    pstmt = Con.prepareStatement(query);
    	    pstmt.setString(1, ParkName);
    	    rs = pstmt.executeQuery();
        if (rs.next()) {
            maxVisitors = rs.getString("MaxVisitors");
            //CurrentVisitors = rs.getString("CurrentVisitors");
            Details=maxVisitors;
    	    } else {
    	        System.out.println("No data found for park: " + ParkName);
    	        Details = "No data found";
    	    }
    	} catch (SQLException e) {
    	    e.printStackTrace();
    	    Details = "Error";
    	} finally {
    	    if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
    	    if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
    	}
    	return Details;
    		 }
    	Details="getMaxVisitors failed";
        return Details;
    	 } 
            		 

    	    public void setWhatToDo(String WhatToDo) {
    	        this.WhatToDo = WhatToDo;
    	    }
    	    public String getWhatToDo() {
    	    	return WhatToDo;
    	    	}
    	    


}
