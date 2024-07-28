package com.airtel.oim.adapter;

import com.airtel.oim.config.DBConfig;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataBase {

    private static final Logger LOG = Logger.getLogger(DataBase.class.getName());
    private static String methodName;

    public Connection getOIMConnection(DBConfig dbConfig) {
        LOG.info("Start :: getOIMConnection()");
        Connection conn = null;

        try {
            // Load the JDBC driver dynamically
            Class.forName(dbConfig.getDriverClassName());
            conn = DriverManager.getConnection(dbConfig.getJdbcUrl(), dbConfig.getUsername(), dbConfig.getPassword());
            LOG.log(Level.INFO, "Database Connected Successfully");
        } catch (ClassNotFoundException e) {
            LOG.log(Level.SEVERE, "JDBC Driver not found", e);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Connection Failed", e);
        }


        return conn;
    }

    public ResultSet executeQuery(DBConfig dbConfig, String query, String... params) throws SQLException {
        Connection connection = getOIMConnection(dbConfig);
        if (connection == null) {
            throw new SQLException("Unable to establish a connection");
        }

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setString(i + 1, params[i]);
        }
        return preparedStatement.executeQuery();
    }

    public void close(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
                LOG.info("End :: resultSet.close()");
            }
            if (preparedStatement != null) {
                preparedStatement.close();
                LOG.info("End :: preparedStatement.close()");
            }
            if (connection != null) {
                connection.close();
                LOG.info("End :: getOIMConnection()");
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Failed to close resources", e);
        }
    }
}
