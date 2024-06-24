package server;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CancleOrderInDB implements Serializable {

    private String orderNo;

    public CancleOrderInDB(String orderNo) {
        this.orderNo = orderNo;
    }

    public String DeleteOrder(Connection con) {
        try {
            String checkOrderSQL = "SELECT * FROM orders WHERE ordernumber = ?";
            PreparedStatement checkOrderStmt = con.prepareStatement(checkOrderSQL);
            checkOrderStmt.setString(1, orderNo);
            ResultSet orderResult = checkOrderStmt.executeQuery();

            if (orderResult.next()) {
                String insertCanceledOrderSQL = "INSERT INTO ordersdata (ordernumber, date, numofvisitors, type, status,parkname) VALUES (?, ?, ?, ?, 'canceled',?)";
                PreparedStatement insertCanceledOrderStmt = con.prepareStatement(insertCanceledOrderSQL);

                insertCanceledOrderStmt.setString(1, orderResult.getString("ordernumber"));
                insertCanceledOrderStmt.setString(2, orderResult.getString("date"));
                insertCanceledOrderStmt.setString(3, orderResult.getString("numberofvisitors"));
                insertCanceledOrderStmt.setString(4, orderResult.getString("type"));
                insertCanceledOrderStmt.setString(5, orderResult.getString("parkname"));

                int rowsInserted = insertCanceledOrderStmt.executeUpdate();

                insertCanceledOrderStmt.close();
                checkOrderStmt.close();

                if (rowsInserted > 0) {
                    String deleteOrderSQL = "DELETE FROM orders WHERE ordernumber = ?";
                    PreparedStatement deleteOrderStmt = con.prepareStatement(deleteOrderSQL);
                    deleteOrderStmt.setString(1, orderNo);

                    int rowsDeleted = deleteOrderStmt.executeUpdate();

                    deleteOrderStmt.close();

                    if (rowsDeleted > 0) {
                        return "Done";
                    } else {
                        return "Error occurred while deleting the order";
                    }
                } else {
                    return "Error occurred while moving the order to ordersdata";
                }
            } else {
                return "No order found with this order number";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
        }
    }

}
