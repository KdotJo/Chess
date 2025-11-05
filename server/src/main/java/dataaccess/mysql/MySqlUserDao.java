package dataaccess.mysql;
import java.sql.*;

import dataaccess.DatabaseManager;
import dataaccess.interfaces.UserDataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;


public class MySqlUserDao implements UserDataAccess {
    public MySqlUserDao() throws DataAccessException {
        configureDatabase();
    }
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
            username varchar(256) NOT NULL,
            password varchar(256) NOT NULL,
            email varchar(256) NOT NULL,
            PRIMARY KEY (username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection connection = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to configure database", e);
        }
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        var createNewUser = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement queryStatement = connection.prepareStatement(createNewUser)) {
            setUserValues(queryStatement, userData);
            queryStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("User Creation Failed", e);
        } ;
    }

    private void setUserValues (PreparedStatement values, UserData user) throws SQLException {
        values.setString(1, user.getUsername());
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        values.setString(2, hashedPassword);
        values.setString(3, user.getEmail());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var query = "SELECT * FROM users WHERE username = ?";
        try (Connection connection  = DatabaseManager.getConnection();
            PreparedStatement queryStatement = connection.prepareStatement(query)) {
            queryStatement.setString(1, username);
            ResultSet qr = queryStatement.executeQuery();
            if (qr.next()) {
                return new UserData(
                        qr.getString("username"),
                        qr.getString("password"),
                        qr.getString("email"));
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get user", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE users";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement queryStatement = conn.prepareStatement(statement)) {
            queryStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear table", e);
        }
    }
}
