package dataaccess.MySqlDAO;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.GameDataAccess;
import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class MySqlGameDAO implements GameDataAccess {

    public MySqlGameDAO () throws DataAccessException {
        configureDatabase();
    }
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
            gameID int NOT NULL AUTO_INCREMENT,
            whiteUsername varchar(256) DEFAULT NULL,
            blackUsername varchar(256) DEFAULT NULL,
            gameName varchar(256) NOT NULL,
            game TEXT DEFAULT NULL,
            PRIMARY KEY (gameID)
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
    public void createGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(int gameID, String whiteUsername, String blackUsername) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
