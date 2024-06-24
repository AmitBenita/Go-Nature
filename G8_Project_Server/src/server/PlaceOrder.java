package server;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class PlaceOrder implements Serializable {
    private String firstName;
    private String lastName;
    private String parkName;
    private String orderNumber;
    private int timeOfVisit;
    private String date;
    private String numOfVisitors;
    private String email;
    private String phoneNum;
    private int openSpots;
    private int minOpenSpots;
    private boolean ifOlderOrdersExist = false;
    private  String whatToDo;
    private double price;
    private String type;

    
    public PlaceOrder(String firstName, String lastName, String parkName, String orderNumber, int timeOfVisit,
            String date, String numOfVisitors, String email, String phoneNum, String whatToDo,String type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        setParkName(parkName);
        setOrderNumber(orderNumber);
        setTimeOfVisit(timeOfVisit);
        setNumOfVisitors(numOfVisitors);
        setEmail(email);
        setPhoneNum(phoneNum);
        setPrice(Integer.parseInt(numOfVisitors),type);
        this.whatToDo=whatToDo;
        this.type=type;
    }

    public String InsertOrderToDB(Connection con) throws SQLException {
    	if(whatToDo.equals("waitingList")) {
    		   String checkQuery = "SELECT MAX(waitNumber) AS maxWaitNumber FROM waitinglist WHERE date = ? AND time = ?";
    	        String insertQuery = "INSERT INTO waitinglist (FirstName, LastName, ParkName, OrderNumber, Time, Date, NumberOfVisitors, Phone, Email, WaitNumber, price, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    	        // Check if there is a row with the same date and time
    	        PreparedStatement ps = con.prepareStatement(checkQuery);
    	        ps.setString(1, date);
    	        ps.setInt(2, timeOfVisit);
    	        ResultSet rs = ps.executeQuery();
    	        int waitNumber = 1; // Default wait number if no rows found with same date and time
    	        if (rs.next()) {
    	            int maxWaitNumber = rs.getInt("maxWaitNumber");
    	            if (!rs.wasNull()) {
    	                waitNumber = maxWaitNumber + Integer.parseInt(numOfVisitors);
    	            }
    	        }

    	        // Insert data into waitinglist table
    	        ps = con.prepareStatement(insertQuery);
    	        ps.setString(1, firstName);
    	        ps.setString(2, lastName);
    	        ps.setString(3, parkName);
    	        ps.setString(4, orderNumber);
    	        ps.setInt(5, timeOfVisit);
    	        ps.setString(6, date);
    	        ps.setString(7, numOfVisitors);
    	        ps.setString(8, phoneNum);
    	        ps.setString(9, email);
    	        ps.setInt(10, waitNumber);
                ps.setDouble(11, price);
                ps.setString(11, type);
    	        ps.executeUpdate();
    	        // Close resources
    	        rs.close();
    	        ps.close();
    		    		    		
    		return "waitingList";
    		}
    	else {
            String ifexistingOrdersQuery = "SELECT COUNT(*) AS existingOrdersCount FROM orders WHERE parkname = ? AND date = ? AND time = ?";
            try (PreparedStatement ifexistingOrdersStmt = con.prepareStatement(ifexistingOrdersQuery)) {
                ifexistingOrdersStmt.setString(1, parkName);
                ifexistingOrdersStmt.setString(2, date);
                ifexistingOrdersStmt.setInt(3, timeOfVisit);
                try (ResultSet ifexistingOrdersResult = ifexistingOrdersStmt.executeQuery()) {
                    if (ifexistingOrdersResult.next()) {
                        int existingOrdersCount = ifexistingOrdersResult.getInt("existingOrdersCount");
                        ifexistingOrdersStmt.close();
                        ifexistingOrdersResult.close();            
                        if (existingOrdersCount > 0) {     	   
                            String existingOrdersQuery = "SELECT MIN(openSpots) AS minOpenSpots FROM orders WHERE parkname = ? AND date = ? AND time = ?";
                            try (PreparedStatement existingOrdersStmt = con.prepareStatement(existingOrdersQuery)) {
                                existingOrdersStmt.setString(1, parkName);
                                existingOrdersStmt.setString(2, date);
                                existingOrdersStmt.setInt(3, timeOfVisit);

                                try (ResultSet existingOrdersResult = existingOrdersStmt.executeQuery()) {
                                    if (existingOrdersResult.next()) {
                                        minOpenSpots = existingOrdersResult.getInt("minOpenSpots");
                                        openSpots = minOpenSpots - Integer.parseInt(numOfVisitors);
                                        if (openSpots < 0) {
                                         return "Error";
                                        }
                                    }
                                }
                            }
                        } else {
                            String selectMaxOrdersQuery = "SELECT MaxOrders FROM parks WHERE parkname = ?";
                            try (PreparedStatement selectMaxOrdersStmt = con.prepareStatement(selectMaxOrdersQuery)) {
                                selectMaxOrdersStmt.setString(1, parkName);
                                try (ResultSet maxOrdersResult = selectMaxOrdersStmt.executeQuery()) {
                                    int maxOrders;
                                    if (maxOrdersResult.next()) {
                                        maxOrders = Integer.parseInt(maxOrdersResult.getString("MaxOrders"));
                                    } else {
                                        return "Error: Could not retrieve MaxOrders value from parks table.";
                                    }
                                    openSpots = maxOrders - Integer.parseInt(numOfVisitors);
                                }
                            }
                        }
                    }
                }
            }         
            return insertOrderWithOpenSpots(con, openSpots);
        } 
      }
   

    private String insertOrderWithOpenSpots(Connection con, int openSpots) throws SQLException {
        // Prepare INSERT query
        String insertOrderQuery = "INSERT INTO orders (ordernumber, Firstname, Lastname, parkname, time, date, numberofvisitors, phone, email, openSpots, price,type) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
        // Set parameters and execute the query
        try (PreparedStatement pstmt = con.prepareStatement(insertOrderQuery)) {
            pstmt.setString(1, orderNumber);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, parkName);
            pstmt.setInt(5, timeOfVisit);
            pstmt.setString(6, date);
            pstmt.setString(7, numOfVisitors);
            pstmt.setString(8, email);
            pstmt.setString(9, phoneNum);
            pstmt.setInt(10, openSpots);
            pstmt.setDouble(11, price);
            pstmt.setString(12, type);



            pstmt.executeUpdate();
            return "Order inserted successfully!";
        } catch (SQLException e) {
            throw new SQLException("Error inserting order: " + e.getMessage());
        }
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber = orderNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTimeOfVisit() {
        return timeOfVisit;
    }

    public void setTimeOfVisit(int timeOfVisit) {
        this.timeOfVisit = timeOfVisit;
    }

    public String getNumOfVisitors() {
        return numOfVisitors;
    }

    public void setNumOfVisitors(String numOfVisitors) {
        this.numOfVisitors = numOfVisitors;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
    public double setPrice(int numofvisitors,String type) {
    	if(type.equals("Personalorder")) //price for personal visitor
    	  this.price=(numofvisitors*100) - ((numofvisitors*100)*(15/100));
    	else if(type.equals("Grouporder")) {
        	this.price=((numofvisitors-1)*100) - ((numofvisitors-1)*100)*(25/100); // price for group
    	}
    	
		return price;
    	
    }
}
