 package server;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ImportOrderDetails implements Serializable {
    private String orderNumber;
    private String orderDetailsString;
    private String status;
    private String query;


    public ImportOrderDetails(String orderNumber,String status) {
        this.orderNumber = orderNumber;
        this.status = status;
    }

    public String importOrderFromDB(Connection con) {
        try {
        	if(status.equals("Waiting")) {
                 query = "SELECT * FROM project.waitinglist WHERE ordernumber = ?"; //for waiting list orders
        	}
        	else  query = "SELECT * FROM project.orders WHERE ordernumber = ?";// for regular orders
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, orderNumber);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(resultSet.getString("ordernumber")).append(" ");
                stringBuilder.append(resultSet.getString("Firstname")).append(" ");
                stringBuilder.append(resultSet.getString("Lastname")).append(" ");
                stringBuilder.append(resultSet.getString("parkname")).append(" ");
                stringBuilder.append(resultSet.getString("time")).append(" ");
                stringBuilder.append(resultSet.getString("date")).append(" ");
                stringBuilder.append(resultSet.getString("numberofvisitors")).append(" ");
                stringBuilder.append(resultSet.getString("phone")).append(" ");
                stringBuilder.append(resultSet.getString("email")).append(" ");
                stringBuilder.append(resultSet.getString("price"));

                orderDetailsString = stringBuilder.toString();
            }            
            resultSet.close();
            statement.close();        	
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderDetailsString;
    }

    public String getOrderDetailsString() {
        return orderDetailsString;
    }
}
