package com.airtel.oim.main;

import com.airtel.oim.adapter.DataBase;
import com.airtel.oim.config.DBConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetStatusOfUserEveryResetChangePassword {
    private static final Logger LOG = Logger.getLogger(GetStatusOfUserEveryResetChangePassword.class.getName());

    public static void main(String[] args) {
        String jdbcUrl = System.getenv("DB_JDBC_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");
        String driverClassName = System.getenv("DB_DRIVER_CLASS_NAME");

        if (jdbcUrl == null || username == null || password == null || driverClassName == null) {
            LOG.severe("Database configuration environment variables are not set.");
            return;
        }

        DBConfig dbConfig = new DBConfig(jdbcUrl, username, password, driverClassName);
        DataBase db = new DataBase();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = db.getOIMConnection(dbConfig);
            if (connection == null) {
                throw new SQLException("Unable to establish a connection");
            }

            String query = "SELECT * FROM users";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String usernameResult = resultSet.getString("username");
                String passwordHash = resultSet.getString("password_hash");
                String email = resultSet.getString("email");
                String createdAt = resultSet.getString("created_at");
                String updatedAt = resultSet.getString("updated_at");

                LOG.info(String.format("ID: %s, Username: %s, Password Hash: %s, Email: %s, Created At: %s, Updated At: %s",
                        id, usernameResult, passwordHash, email, createdAt, updatedAt));
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Query Execution Failed", e);
        } finally {
            db.close(connection, preparedStatement, resultSet);
        }
    }
}
