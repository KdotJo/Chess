package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.JoinGameResult;

import java.util.UUID;

public class GameService {
    private final AuthDAO authDao;
    private final GameDAO gameDao;

    public GameService(AuthDAO authDao, GameDAO gameDao) {
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    public CreateGameResult create(String authToken, CreateGameRequest createGameRequest) throws DataAccessException {
        if (authDao.getAuth(authToken) == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        int gameId = Math.abs(UUID.randomUUID().hashCode());
        String username = authDao.getAuth(authToken).getUsername();
        ChessGame newGame = new ChessGame();
        GameData gameData = new GameData(gameId, username, null, createGameRequest.gameName(), newGame);
        gameDao.createGame(gameData);
        return new CreateGameResult(gameId);
    }

    public JoinGameResult join(String authToken, JoinGameRequest joinGameRequest) throws DataAccessException {
        if (authDao.getAuth(authToken) == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        GameData getGame = gameDao.getGame(joinGameRequest.gameID());
        String teamColor = joinGameRequest.playerColor();
        String username = authDao.getAuth(authToken).getUsername();
        if (getGame == null) {
            throw new DataAccessException("Error: Bad Request");
        }
        if (teamColor.equals("WHITE")){
            if (getGame.getWhiteUsername() != null) {
                throw new DataAccessException("Error: Team Already Taken");
            }
            gameDao.updateGame(getGame.getGameID(), username, getGame.getWhiteUsername());
        }
         if (teamColor.equals("BLACK")) {
             if (getGame.getBlackUsername() == null) {
                 throw new DataAccessException("Error: Team Already Taken");
             }
            gameDao.updateGame(getGame.getGameID(), getGame.getBlackUsername(), username);
         }
         if (!teamColor.equals("WHITE") && !teamColor.equals("BlACK")) {
             throw new DataAccessException("Error: Invalid Team");
         }
        return new JoinGameResult();
    }
}
