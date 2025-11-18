package dataaccess.mysql;

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
