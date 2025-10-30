package service;

import chess.ChessGame;
import dataaccess.MemoryAuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryGameDAO;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;

import java.util.Collection;
import java.util.UUID;

public class GameService {
    private final MemoryAuthDAO memoryAuthDao;
    private final MemoryGameDAO memoryGameDao;

    public GameService(MemoryAuthDAO memoryAuthDao, MemoryGameDAO memoryGameDao) {
        this.memoryAuthDao = memoryAuthDao;
        this.memoryGameDao = memoryGameDao;
    }

    public ListGamesResult list(String authToken) throws DataAccessException {
        if (memoryAuthDao.getAuth(authToken) == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        Collection<GameData> games = memoryGameDao.listGames();
        return new ListGamesResult(games);
    }

    public CreateGameResult create(String authToken, CreateGameRequest createGameRequest) throws DataAccessException {
        if (memoryAuthDao.getAuth(authToken) == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        int gameId = Math.abs(UUID.randomUUID().hashCode());
        ChessGame newGame = new ChessGame();
        GameData gameData = new GameData(gameId, null, null, createGameRequest.gameName(), newGame);
        memoryGameDao.createGame(gameData);
        return new CreateGameResult(gameId);
    }

    public JoinGameResult join(String authToken, JoinGameRequest joinGameRequest) throws DataAccessException {

        if (memoryAuthDao.getAuth(authToken) == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        GameData getGame = memoryGameDao.getGame(joinGameRequest.gameID());
        String teamColor = joinGameRequest.playerColor();
        String username = memoryAuthDao.getAuth(authToken).getUsername();
        if (getGame == null) {
            throw new DataAccessException("Error: Bad Request");
        }
        if (teamColor.equals("WHITE")) {
            if (getGame.getWhiteUsername() != null && !getGame.getWhiteUsername().equals(username)) {
                throw new DataAccessException("Error: Team Already Taken");
            }
            memoryGameDao.updateGame(getGame.getGameID(), username, getGame.getBlackUsername());
        }
        if (teamColor.equals("BLACK")) {
            if (getGame.getBlackUsername() != null && !getGame.getBlackUsername().equals(username)) {
                throw new DataAccessException("Error: Team Already Taken");
            }
            memoryGameDao.updateGame(getGame.getGameID(), getGame.getWhiteUsername(), username);
        }
         if (!teamColor.equals("WHITE") && !teamColor.equals("BLACK")) {
             throw new DataAccessException("Error: Invalid Team");
         }
        return new JoinGameResult();
    }
}
