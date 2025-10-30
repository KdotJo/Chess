package dataaccess.MySqlDAO;
import java.sql.*;

import dataaccess.DatabaseManager;
import dataaccess.interfaces.UserDataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;


public class MySqlUserDAO implements UserDataAccess {

    public MySqlUserDAO() throws DataAccessException, SQLException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
            'username' varchar(256) NOT NULL,
            'password' varchar(256) NOT NULL,
            'email' varchar(256) NOT NULL,
            PRIMARY KEY ('username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws SQLException, DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection connection = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Unable to configure database", ex.getMessage());
        }
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        var query = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement queryStatement = connection.prepareStatement(query)) {
            queryStatement.setString(1, userData.getUsername());
            ResultSet queryResults = queryStatement.executeQuery();
            if (queryResults.next()) {
                throw new DataAccessException("Username Already Taken");
            }
            var createNewUser = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            PreparedStatement insertUserStatement = connection.prepareStatement(createNewUser);
            setUserValues(insertUserStatement, userData);
            int results = insertUserStatement.executeUpdate();
            if (results != 1) {
                throw new DataAccessException("User Creation Failed");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unauthorized Error", e);
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
        return null;
    }

    @Override
    public boolean usernameExists(String username) throws DataAccessException {
        return false;
    }

    @Override
    public void clear() throws DataAccessException {

    }


}
