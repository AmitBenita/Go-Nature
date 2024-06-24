package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class Order {
    int orderId;
    String datetime;
    String email;
    String phone;

    public Order(int orderId, String datetime, String email, String phone) {
        this.orderId = orderId;
        this.datetime = datetime;
        this.email = email;
        this.phone = phone;
    }
}

public class OrderReminder {
    private List<Order> orders;

    public OrderReminder() {
        orders = new ArrayList<>();
    }

    public void sendReminder(Connection connection) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d\\M\\yyyy HH");
        Date now = new Date();

        try {
            String query = "SELECT ordernumber, date, mail, phone FROM orders";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int orderId = resultSet.getInt("ordernumber");
                String datetime = resultSet.getString("date");
                String email = resultSet.getString("mail");
                String phone = resultSet.getString("phone");
                orders.add(new Order(orderId, datetime, email, phone));
            }

            for (Order order : orders) {
                Date orderDate = dateFormat.parse(order.datetime);
                long timeDiff = orderDate.getTime() - now.getTime();
                int hoursDiff = (int) (timeDiff / (60 * 60 * 1000) % 24);

                if (hoursDiff == 24) {
                    System.out.println("Reminder: Order " + order.orderId + " is coming up on " + order.datetime + "!");
                    System.out.println("Get ready to deliver happiness!");
                    // Add code here to send email or message
                }
            }

            resultSet.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
    	/*
    	//Reminder 24h before order date 
        try {
        	while(true) {
        		 Thread.sleep(3600 * 1000);	//sleep 1 hr
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "Salman16!");

            // Create an instance of OrderReminder
            OrderReminder reminder = new OrderReminder();

            // Call sendReminder method
            reminder.sendReminder(connection);

            // Close the connection
            connection.close();
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }
}
