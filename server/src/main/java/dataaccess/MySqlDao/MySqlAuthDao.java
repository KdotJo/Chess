package dataaccess.MySqlDao;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.AuthDataAccess;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySqlAuthDao implements AuthDataAccess {

    public MySqlAuthDao() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS authTokens (
            authToken varchar(256) NOT NULL,
            username varchar(256) NOT NULL,
            PRIMARY KEY (authToken)
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
    public String createAuth(String username) throws DataAccessException {
        var query = "INSERT INTO authTokens (authToken, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            String authToken = UUID.randomUUID().toString();
            preparedStatement.setString(1, authToken);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
            return authToken;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create authToken", e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var query = "SELECT * FROM authTokens WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, authToken);
            ResultSet results = preparedStatement.executeQuery();
            if (results.next()) {
                return new AuthData(
                        results.getString("authToken"),
                        results.getString("username")
                );
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get authToken", e);
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var query = "DELETE FROM authTokens WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, authToken);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete authToken", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE authTokens";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement queryStatement = conn.prepareStatement(statement)) {
            queryStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear table", e);
        }
    }
}
