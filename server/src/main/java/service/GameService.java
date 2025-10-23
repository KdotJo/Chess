package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import request.CreateGameRequest;
import result.CreateGameResult;

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
}
