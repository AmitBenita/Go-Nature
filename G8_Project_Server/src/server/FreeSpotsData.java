package server;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FreeSpotsData   implements Serializable{
    
	public static String ImportFreeSpotsFromDB(Connection con) {
	    StringBuilder result = new StringBuilder();
	    try {
	        Statement stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery(
	            "SELECT parkname, date, time, MIN(openSpots) AS minOpenSpots " +
	            "FROM orders " +
	            "GROUP BY parkname, date, time"
	        );

	        while (rs.next()) {
	            int minOpenSpots = rs.getInt("minOpenSpots");
	                String parkname = rs.getString("parkname");
	                String date = rs.getString("date");
	                String time = rs.getString("time");
	                
	                result.append(parkname).append(" ").append(date).append(" ").append(time).append(" ").append(minOpenSpots).append(",");
	         
	        }            
	        rs.close();
	        stmt.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    if (result.length() > 0) {
	        result.deleteCharAt(result.length() - 1);
	    }
	    
	    return result.toString();
	}

}














