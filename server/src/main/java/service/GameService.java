    package service;

    import chess.ChessBoard;
    import chess.ChessGame;
    import com.google.gson.Gson;
    import dataaccess.interfaces.AuthDataAccess;
    import dataaccess.interfaces.GameDataAccess;
    import dataaccess.DataAccessException;
    import model.GameData;
    import request.CreateGameRequest;
    import request.JoinGameRequest;
    import result.*;

    import java.util.Collection;
    import java.util.UUID;

    public class GameService {
        private final AuthDataAccess authDao;
        private final GameDataAccess gameDao;

        public GameService(AuthDataAccess authDao, GameDataAccess gameDao) {
            this.authDao = authDao;
            this.gameDao = gameDao;
        }

        public ListGamesResult list(String authToken) throws DataAccessException {
            if (authDao.getAuth(authToken) == null) {
                throw new DataAccessException("Error: Missing authToken");
            }
            Collection<GameData> games = gameDao.listGames();
            return new ListGamesResult(games);
        }

        public CreateGameResult create(String authToken, CreateGameRequest createGameRequest) throws DataAccessException {
            if (authDao.getAuth(authToken) == null) {
                throw new DataAccessException("Error: Missing authToken");
            }
            int gameId = Math.abs(UUID.randomUUID().hashCode());
            ChessGame newGame = new ChessGame();
            GameData gameData = new GameData(gameId, null, null, createGameRequest.gameName(), newGame);
            gameDao.createGame(gameData);
            return new CreateGameResult(gameId);
        }

        public JoinGameResult join(String authToken, JoinGameRequest joinGameRequest) throws DataAccessException {
            if (authDao.getAuth(authToken) == null) {
                throw new DataAccessException("Error: Missing authToken");
            }
            GameData getGame = gameDao.getGame(joinGameRequest.gameID());
            String teamColor = joinGameRequest.playerColor();
            String username = authDao.getAuth(authToken).getUsername();
            if (getGame == null) {
                throw new DataAccessException("Error: Can't Get Game");
            }
            if (teamColor.equals("WHITE") && !joinGameRequest.role().equals("spectate")) {
                if (getGame.getBlackUsername() != null && getGame.getBlackUsername().equals(username)) {
                    gameDao.updateGame(getGame.getGameID(), username, null);
                }
                else if (getGame.getWhiteUsername() != null && !getGame.getWhiteUsername().equals(username)) {
                    throw new DataAccessException("Error: White Team Already Taken");
                }
                else {
                    gameDao.updateGame(getGame.getGameID(), username, getGame.getBlackUsername());
                }
            }
            if (teamColor.equals("BLACK") && !joinGameRequest.role().equals("spectate")) {
                if (getGame.getWhiteUsername() != null && getGame.getWhiteUsername().equals(username)) {
                    gameDao.updateGame(getGame.getGameID(), null, username);
                }
                else if (getGame.getBlackUsername() != null && !getGame.getBlackUsername().equals(username)) {
                    throw new DataAccessException("Error: Black Team Already Taken");
                }
                else {
                    gameDao.updateGame(getGame.getGameID(), getGame.getWhiteUsername(), username);
                }
            }
            return new JoinGameResult(gameDao.getGame(joinGameRequest.gameID()));
        }
        public ClearResult clear() throws DataAccessException {
            try {
                gameDao.clear();
                authDao.clear();
                return new ClearResult();
            } catch (DataAccessException e) {
                throw new DataAccessException("Error: Clear Failed");
            }
        }
    }
