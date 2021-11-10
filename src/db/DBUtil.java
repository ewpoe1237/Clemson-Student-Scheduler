package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    private static Connection connection;

    private DBUtil() {}

    public static synchronized Connection getConnection() throws DBException {
        if (connection != null) {
            return connection;
        }
        else {
            try {
                // set the db url, username, and password
                String url = "jdbc:mysql://database-setup-walkthrough.ch35q4vibozj.us-east-1.rds.amazonaws.com/cpsc2810schema";
                String username = "admin";
                String password = "jhofset1";
                Class.forName("com.mysql.cj.jdbc.Driver");

                // get and return connection
                connection = DriverManager.getConnection(
                        url, username, password);

                return connection;
            } catch (SQLException | ClassNotFoundException e) {
                throw new DBException(e);
            }
        }
    }

    public static synchronized void closeConnection() throws DBException {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DBException(e);
            } finally {
                connection = null;
            }
        }
    }
}