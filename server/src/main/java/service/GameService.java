package service;

import chess.ChessGame;
import dataaccess.interfaces.AuthDataAccess;
import dataaccess.interfaces.GameDataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.ClearResult;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;

import java.util.Collection;
import java.util.UUID;

public class GameService {
    private final AuthDataAccess AuthDao;
    private final GameDataAccess GameDao;

    public GameService(AuthDataAccess AuthDao, GameDataAccess GameDao) {
        this.AuthDao = AuthDao;
        this.GameDao = GameDao;
    }

    public ListGamesResult list(String authToken) throws DataAccessException {
        if (AuthDao.getAuth(authToken) == null) {
            throw new DataAccessException("Error: Missing authToken");
        }
        Collection<GameData> games = GameDao.listGames();
        return new ListGamesResult(games);
    }

    public CreateGameResult create(String authToken, CreateGameRequest createGameRequest) throws DataAccessException {
        if (AuthDao.getAuth(authToken) == null) {
            throw new DataAccessException("Error: Missing authToken");
        }
        int gameId = Math.abs(UUID.randomUUID().hashCode());
        ChessGame newGame = new ChessGame();
        GameData gameData = new GameData(gameId, null, null, createGameRequest.gameName(), newGame);
        GameDao.createGame(gameData);
        return new CreateGameResult(gameId);
    }

    public JoinGameResult join(String authToken, JoinGameRequest joinGameRequest) throws DataAccessException {

        if (AuthDao.getAuth(authToken) == null) {
            throw new DataAccessException("Error: Missing authToken");
        }
        GameData getGame = GameDao.getGame(joinGameRequest.gameID());
        String teamColor = joinGameRequest.playerColor();
        String username = AuthDao.getAuth(authToken).getUsername();
        if (getGame == null) {
            throw new DataAccessException("Error: Can't Get Game");
        }
        if (teamColor.equals("WHITE")) {
            if (getGame.getWhiteUsername() != null && !getGame.getWhiteUsername().equals(username)) {
                throw new DataAccessException("Error: White Team Already Taken");
            }
            GameDao.updateGame(getGame.getGameID(), username, getGame.getBlackUsername());
        }
        if (teamColor.equals("BLACK")) {
            if (getGame.getBlackUsername() != null && !getGame.getBlackUsername().equals(username)) {
                throw new DataAccessException("Error: Black Team Already Taken");
            }
            GameDao.updateGame(getGame.getGameID(), getGame.getWhiteUsername(), username);
        }
        return new JoinGameResult();
    }
    public ClearResult clear() throws DataAccessException {
        try {
            GameDao.clear();
            AuthDao.clear();
            return new ClearResult();
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: Clear Failed");
        }
    }
}
