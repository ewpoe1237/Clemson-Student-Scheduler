package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    private static Connection connection;

    private DBUtil() {}

    public static synchronized Connection getConnection() throws SQLException {
        if (connection != null) {
            return connection;
        }
        else {
            try {
                // set the db url, username, and password
                String url = "jdbc:mysql://scheduler-db.ch35q4vibozj.us-east-1.rds.amazonaws.com/schedulerschema";
                String username = "admin";
                String password = "^Eead7aNU;v-94";
                Class.forName("com.mysql.cj.jdbc.Driver");

                // get and return connection
                connection = DriverManager.getConnection(
                        url, username, password);

                return connection;
            } catch (SQLException | ClassNotFoundException e) {
                throw new SQLException(e);
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