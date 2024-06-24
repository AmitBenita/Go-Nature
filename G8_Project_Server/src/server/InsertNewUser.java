package server;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InsertNewUser implements Serializable{
    boolean GuidOrNot;
    String username;
    String password;
    String[] str;
    String Alreadyexist = "error user already a member";
    String isGuider = "error Guider";
    String success = "Register success";

    public InsertNewUser(String[] userDataString,boolean isGuider) {
        this.str = userDataString;
        this.GuidOrNot=isGuider;
    }

    public String registerUser(Connection con) throws SQLException {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM users WHERE user = ?");
            preparedStatement.setString(1, str[0]);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Alreadyexist; // User already exists
            } else {
                // Insert new user
                preparedStatement = con.prepareStatement("INSERT INTO users (user, password,type,status,IsLogedin) VALUES (?,?,?,?,0)");
                preparedStatement.setString(1, str[0]);
                preparedStatement.setString(2, str[1]);
                if(GuidOrNot) {
                   preparedStatement.setString(3,"Guide");
                   preparedStatement.setString(4,"aprooved");
                     }
                  
                else {
                	preparedStatement.setString(3, "customer");
                    preparedStatement.setString(4,"aprooved");
                }
                preparedStatement.executeUpdate();

                if (GuidOrNot) 
                    return isGuider; // Return Guider
                 else 
                    return success; // Return success
                
            }
        }
    }
