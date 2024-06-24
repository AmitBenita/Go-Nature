package server;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DisconnectUser implements Serializable {

    private String user;

    public DisconnectUser( String user) {
        this.user = user;
    }

    public String disconnectFromDB(Connection con) {
        try {
            String sql = "UPDATE users SET IsLogedin = 0 WHERE user = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, this.user);           
            pstmt.executeUpdate();

            pstmt.close();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
		return "";
    }
}