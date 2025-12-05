package dataaccess.interfaces;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.GameData;

import java.util.Collection;

public interface GameDataAccess {

    //    game related interface requirements
    void createGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(int gameID, String whiteUsername, String blackUsername, ChessGame game) throws DataAccessException;

    void clear() throws DataAccessException;
}
