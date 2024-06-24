package server;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ParkEntrance implements Serializable {
    private String parkName;
    private String whatToDo;
	private String type;

    public ParkEntrance(String parkName, String whatToDo) {
        this.parkName = parkName;
        this.whatToDo = whatToDo;
    }

    // Getters and Setters
    public String getParkName() {
        return parkName;
    }

    public void setWhatToDo(String whatToDo) {
        this.whatToDo = whatToDo;
    }

    public String ParkEntranceDB(Connection connection) throws SQLException {
        if (whatToDo.equals("start")) {
            // Check if the parkname already exists in the livedata table
            String checkQuery = "SELECT parkname FROM livedata WHERE parkname = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setString(1, getParkName());
                try (ResultSet checkResultSet = checkStatement.executeQuery()) {
                    if (checkResultSet.next()) {
                        // Parkname already exists in the livedata table, return error message
                        return "error open";
                    }
                }
            }           
            // Parkname does not exist, insert new data into the livedata table
            String insertQuery = "INSERT INTO livedata (parkname, date, currentvisitors, ordervisitors, regularvisitors, totalincome) VALUES (?, DATE_FORMAT(NOW(), '%d/%m/%y'), 0, 0, 0, 0)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setString(1, getParkName());
                insertStatement.executeUpdate();
            }             
            String maxVisitors = null;
            String query = "SELECT MaxVisitors FROM parks WHERE parkname = ?";
            
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, getParkName());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        maxVisitors = resultSet.getString("MaxVisitors");
                    }
                }
            }       
            return maxVisitors;        
        } 
        else if (whatToDo.contains("update")) {
            try {
                // Split the data string into individual values
                String[] dataValues = whatToDo.split("\\s+");

                // Prepare the update query
                String updateQuery = "UPDATE livedata SET totalincome = ?, currentvisitors = ?, ordervisitors = ?, regularvisitors = ? WHERE parkname = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    // Set parameters for the update statement
                    updateStatement.setString(1, dataValues[1]); // totalincome
                    updateStatement.setString(2, dataValues[2]); // currentvisitors
                    updateStatement.setString(3, dataValues[3]); // ordervisitors
                    updateStatement.setString(4, dataValues[4]); // regularvisitors
                    updateStatement.setString(5, parkName); // parkname

                    // Execute the update statement
                    updateStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle any SQL exceptions here
            }
        }
        else if (whatToDo.contains("EnterReports")) {
            String insertQuery = "INSERT INTO enterdata (parkname, date, type, numberofvisitors, entertime, visitorNumber) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setString(1, parkName); 
                insertStatement.setString(2, LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yy")));
                String[] dataValues = whatToDo.split("\\s+");
                insertStatement.setString(3, dataValues[1]); 
                insertStatement.setString(4, dataValues[2]); 
                insertStatement.setString(5, dataValues[3]); 
                insertStatement.setString(6, dataValues[4]); 
                insertStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (whatToDo.contains("Exit")) {
            // Extract the customer ID from whatToDo
            String[] split = whatToDo.split(" ");
            String customerID = split[1];

            try {
                // Update exit time and remove visitor number from enterdata table
                LocalTime currentTime = LocalTime.now();
                String currentTimeString = String.format("%02d:%02d:%02d", currentTime.getHour(), currentTime.getMinute(), currentTime.getSecond());
                String updateQuery = "UPDATE enterdata SET exittime = ? WHERE visitorNumber = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    updateStatement.setString(1, currentTimeString);
                    updateStatement.setString(2, customerID);
                    updateStatement.executeUpdate();
                }

                // Retrieve the visitor count for the customer
                String selectVisitorCountQuery = "SELECT numberOfVisitors FROM enterdata WHERE visitorNumber = ?";
                try (PreparedStatement selectVisitorCountStatement = connection.prepareStatement(selectVisitorCountQuery)) {
                    selectVisitorCountStatement.setString(1, customerID);
                    try (ResultSet resultSet = selectVisitorCountStatement.executeQuery()) {
                        if (resultSet.next()) {
                            String visitorCount = resultSet.getString("numberOfVisitors");
                            return "num "+visitorCount;
                        } else {
                            return "Error: Visitor ID not found in the database.";
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return "Error occurred while updating exit time and removing visitor number for customer: " + customerID;
           
            }
        }
        else if (whatToDo.contains("orderenter")) {
            // Split the data string into individual values
            String[] split = whatToDo.split(" ");
            // Extract the second element from the split array
            String orderNumber = split[1];

            try {
                // Check if the order number exists in the orders table
                String checkQuery = "SELECT numberofvisitors, price ,type FROM orders WHERE ordernumber = ?";
                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setInt(1, Integer.parseInt(orderNumber));
                ResultSet checkResult = checkStatement.executeQuery();

                // If the order exists, return numberofvisitors value
                if (checkResult.next()) {
                    int numberOfVisitors = checkResult.getInt("numberofvisitors");
                    int price = checkResult.getInt("price");
                     type =checkResult.getString("type");

                    // Update enterdata table
                    String updateQuery = "INSERT INTO enterdata (parkname, date, type, numberofvisitors, entertime, visitorNumber,totalincome) VALUES (?, ?, ?, ?, ?, ?,?)";
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                        updateStatement.setString(1, parkName);
                        updateStatement.setString(2, LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yy")));
                        updateStatement.setString(3, type);
                        updateStatement.setInt(4, numberOfVisitors);
                        updateStatement.setString(5, LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                        updateStatement.setString(6, orderNumber);
                        updateStatement.executeUpdate();
                    }
                    
                    // Delete the order entry from orders table
                    String deleteQuery = "DELETE FROM orders WHERE ordernumber = ?";
                    try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                        deleteStatement.setInt(1, Integer.parseInt(orderNumber));
                        deleteStatement.executeUpdate();
                    }

                    return numberOfVisitors + " " + price;
                } else {
                    // If the order doesn't exist, return error message
                    return "orderDontExist";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return "Error occurred while checking order existence.";
            }
        }
        else if (whatToDo.equals("Endday")) {
            try {
                // Delete the row containing the parkName variable in the parkname column of the livedata table
                String deleteQuery = "DELETE FROM livedata WHERE parkname = ?";
                try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                    deleteStatement.setString(1, parkName);
                    deleteStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle any SQL exceptions here
            }
        }


        return "";
    }
}
