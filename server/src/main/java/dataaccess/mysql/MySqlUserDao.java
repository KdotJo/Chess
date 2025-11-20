package dataaccess.mysql;
import java.sql.*;

import dataaccess.DatabaseHelper;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.UserDataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;


public class MySqlUserDao implements UserDataAccess {

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        var query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        DatabaseHelper.executeUpdate(query, "User Creation Failed", ps -> setUserValues(ps, userData));
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
