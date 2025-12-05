package dataaccess.mysql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseHelper;
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

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        var query = "INSERT INTO games (gameId, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        DatabaseHelper.executeUpdate(query, "Failed to create game", ps -> setGameValues(ps, gameData));
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
    public void updateGame(int gameID, String whiteUsername, String blackUsername, ChessGame game) throws DataAccessException {
        var query = "UPDATE games SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, whiteUsername);
            preparedStatement.setString(2, blackUsername);
            String json = new Gson().toJson(game);
            preparedStatement.setString(3, json);
            preparedStatement.setInt(4, gameID);
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
