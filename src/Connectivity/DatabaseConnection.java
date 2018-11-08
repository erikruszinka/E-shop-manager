package Connectivity;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public Connection connection;
    public Connection getConnection(){
        String username = "root";
        String password = "Erchi123#";

        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/eshop",username,password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
