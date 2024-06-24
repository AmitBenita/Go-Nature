package server;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DoNotApproveRequest  implements Serializable {
	private String id;
	
	public DoNotApproveRequest(String id) {
		this.id=id;
	}


	public String changeStatusToApproved(Connection con) {
		String status = "notApproved";

        try {
            String sql = "UPDATE requests SET status = ? WHERE id = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setString(2, id);
            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            if (rowsAffected > 0) {
                return "notApproved";
            } else {
                return "ID not found";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error updating status";
        }
	}
}

