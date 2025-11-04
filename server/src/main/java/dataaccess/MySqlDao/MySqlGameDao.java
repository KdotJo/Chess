package dataaccess.MySqlDao;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.GameDataAccess;
import model.GameData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class MySqlGameDao implements GameDataAccess {

    public MySqlGameDao() throws DataAccessException {
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
        var createNewGame = "INSERT INTO games " +
                "(gameId, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(createNewGame)) {
            setGameValues(preparedStatement, gameData);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create game", e);
        }
    }

    private void setGameValues (PreparedStatement values, GameData gameData) throws SQLException {
        values.setInt(1, gameData.getGameID());
        values.setString(2, gameData.getWhiteUsername());
        values.setString(3, gameData.getBlackUsername());
        values.setString(4, gameData.getGameName());
        String json = new Gson().toJson(gameData.getGame());
        values.setString(5, json);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var query = "SELECT * FROM games WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setInt(1, gameID);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                String json = result.getString("game");
                ChessGame game = new Gson().fromJson(json, ChessGame.class);
                return new GameData(result.getInt("gameID"), result.getString("whiteUsername"),
                        result.getString("blackUsername"), result.getString("gameName"),
                        game);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get game", e);
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var query = "SELECT * FROM games";
        Collection<GameData> allGames = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            ResultSet results = preparedStatement.executeQuery();
            while (results.next()) {
                int gameId = results.getInt("gameID");
                String whiteUsername = results.getString("whiteUsername");
                String blackUsername = results.getString("blackUsername");
                String gameName = results.getString("gameName");
                String json = results.getString("game");
                ChessGame game = new Gson().fromJson(json, ChessGame.class);
                allGames.add(new GameData(gameId, whiteUsername, blackUsername,
                        gameName, game));
            }
            return allGames;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list games", e);
        }
    }

    @Override
    public void updateGame(int gameID, String whiteUsername, String blackUsername) throws DataAccessException {
        var whiteQuery = "UPDATE games SET whiteUsername = ?, blackUsername = ? WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(whiteQuery)) {
            preparedStatement.setString(1, whiteUsername);
            preparedStatement.setString(2, blackUsername);
            preparedStatement.setInt(3, gameID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update game", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement queryStatement = conn.prepareStatement(statement)) {
            queryStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear table", e);
        }
    }
}
