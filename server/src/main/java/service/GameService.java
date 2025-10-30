package service;

import chess.ChessGame;
import dataaccess.interfaces.AuthDataAccess;
import dataaccess.interfaces.GameDataAccess;
import dataaccess.memoryDAO.MemoryAuthDAO;
import dataaccess.DataAccessException;
import dataaccess.memoryDAO.MemoryGameDAO;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
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
            throw new DataAccessException("Error: Unauthorized");
        }
        Collection<GameData> games = GameDao.listGames();
        return new ListGamesResult(games);
    }

    public CreateGameResult create(String authToken, CreateGameRequest createGameRequest) throws DataAccessException {
        if (AuthDao.getAuth(authToken) == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        int gameId = Math.abs(UUID.randomUUID().hashCode());
        ChessGame newGame = new ChessGame();
        GameData gameData = new GameData(gameId, null, null, createGameRequest.gameName(), newGame);
        GameDao.createGame(gameData);
        return new CreateGameResult(gameId);
    }

    public JoinGameResult join(String authToken, JoinGameRequest joinGameRequest) throws DataAccessException {

        if (AuthDao.getAuth(authToken) == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        GameData getGame = GameDao.getGame(joinGameRequest.gameID());
        String teamColor = joinGameRequest.playerColor();
        String username = AuthDao.getAuth(authToken).getUsername();
        if (getGame == null) {
            throw new DataAccessException("Error: Bad Request");
        }
        if (teamColor.equals("WHITE")) {
            if (getGame.getWhiteUsername() != null && !getGame.getWhiteUsername().equals(username)) {
                throw new DataAccessException("Error: Team Already Taken");
            }
            GameDao.updateGame(getGame.getGameID(), username, getGame.getBlackUsername());
        }
        if (teamColor.equals("BLACK")) {
            if (getGame.getBlackUsername() != null && !getGame.getBlackUsername().equals(username)) {
                throw new DataAccessException("Error: Team Already Taken");
            }
            GameDao.updateGame(getGame.getGameID(), getGame.getWhiteUsername(), username);
        }
         if (!teamColor.equals("WHITE") && !teamColor.equals("BLACK")) {
             throw new DataAccessException("Error: Invalid Team");
         }
        return new JoinGameResult();
    }
}
