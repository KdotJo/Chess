package client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exceptions.ServerFacadeException;
import model.GameData;
import result.*;

import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade serverFacade;
    private State state = State.SIGNEDOUT;
    private String authToken;

    public ChessClient(String serverUrl) throws ServerFacadeException {
        serverFacade = new ServerFacade(serverUrl);
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL> (Please create an account)
                    - login <USERNAME> <PASSWORD> (Login to play)
                    - help (List of commands)
                    - quit
                    """;
        }
        return """
                - create <NAME> (Creating a game)
                - list (List games)
                - join [WHITE][BLACK] <ID> (Joining a game)
                - spectate <ID> (Spectate a game)
                - help (List of commands)
                - logout (Logout of your account)
                - quit
                """;
    }

    public static String getPiece(ChessPiece.PieceType type, ChessGame.TeamColor color) {
        String types = type.toString().toUpperCase();
        String colors = color.toString().toUpperCase();

        switch (colors) {
            case "WHITE":
                switch (types) {
                    case "KING": return WHITE_KING;
                    case "QUEEN": return WHITE_QUEEN;
                    case "PAWN": return WHITE_PAWN;
                    case "KNIGHT": return WHITE_KNIGHT;
                    case "ROOK": return WHITE_ROOK;
                    case "BISHOP": return WHITE_BISHOP;
                }
                break;
            case "BLACK":
                switch (types) {
                    case "KING": return BLACK_KING;
                    case "QUEEN": return BLACK_QUEEN;
                    case "PAWN": return BLACK_PAWN;
                    case "KNIGHT": return BLACK_KNIGHT;
                    case "ROOK": return BLACK_ROOK;
                    case "BISHOP": return BLACK_BISHOP;
                }
                break;
        }
        return EMPTY;
    }

    private ArrayList<GameData> gameList;

    public void board(ChessPiece[][] board, String color) {
        String letters = "    h  g  f  e  d  c  b  a ";
        boolean whiteView = false;
        if (color.equals("WHITE")){
            letters = "    a  b  c  d  e  f  g  h ";
            ChessPiece[][] whiteBoard = new ChessPiece[8][8];
            for (int row = 0; row < 8; row++) {
                whiteBoard[row] = board[7-row];
                whiteView = true;
            }
            board = whiteBoard;
        }
        System.out.println(SET_TEXT_BOLD + letters + RESET_TEXT_BOLD_FAINT);
        String pieceType;
        for (int row = 0; row < 8; row++) {
            int rank = whiteView ? 8 - row : 1 + row;
            System.out.print(SET_TEXT_BOLD + " " + rank + " " + RESET_TEXT_BOLD_FAINT);
            for (int j = 0; j < 8; j++) {
                int col = whiteView ? j : 7 - j;
                ChessPiece piece = board[row][col];
                boolean whiteSquare = (row + j) % 2 == 0;
                    if (piece == null) {
                        pieceType = EMPTY;
                    } else {
                        pieceType = getPiece(piece.getPieceType(), piece.getTeamColor());
                    }
                    String bg = whiteSquare ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLUE;
                    System.out.print(bg + pieceType + RESET_BG_COLOR);
            }
            System.out.print(SET_TEXT_BOLD + " " + rank + " " + RESET_TEXT_BOLD_FAINT);
            System.out.println();
        }
        System.out.println(SET_TEXT_BOLD + letters + RESET_TEXT_BOLD_FAINT);
    }

    private void display(ChessGame result, String message, String color) throws ServerFacadeException {
        System.out.print(message);
        if (result != null && result.getBoard() != null) {
            ChessBoard cb = result.getBoard();
            ChessPiece[][] pieces = new ChessPiece[8][8];
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    pieces[row-1][col-1] = cb.getPiece(new ChessPosition(row, col));
                }
            }
            board(pieces, color);
        }
    }

    private String loggedOut(String input) {
        String[] commands = input.split("\\s+");
        if (commands.length == 0 || commands[0].isEmpty()) {return "";}
        String lower = commands[0].toLowerCase();
        switch (lower) {
            case "register":
                if (commands.length < 4) {
                    System.out.print("Please Enter Fields: register <USERNAME> <PASSWORD> <EMAIL>");
                    return "";
                }
                try {
                    RegisterResult result = serverFacade.register(commands[1], commands[2], commands[3]);
                    this.authToken = result.authToken();
                    if (result.authToken() != null) {
                        System.out.println(SET_TEXT_ITALIC + SET_TEXT_COLOR_MAGENTA +
                                "Registration Successful! Welcome " + result.username() +
                                RESET_TEXT_ITALIC + RESET_TEXT_COLOR);
                        state = State.SIGNEDIN;
                        System.out.print(help());
                    } else {
                        System.out.print("Registration Failed: Internal Server Failure");
                    }
                } catch (ServerFacadeException e) {
                    System.out.print("Registration Failed: " + e.getMessage());
                }
                break;
            case "login":
                if (commands.length < 3) {
                    System.out.print("Please Enter Fields: login <USERNAME> <PASSWORD>");
                    return "";
                }
                try {
                    LoginResult result = serverFacade.login(commands[1], commands[2]);
                    this.authToken = result.authToken();
                    if (result.authToken() != null) {
                        System.out.println(SET_TEXT_ITALIC + SET_TEXT_COLOR_MAGENTA +
                                "Login Successful! Welcome " + result.username() +
                                RESET_TEXT_ITALIC + RESET_TEXT_COLOR);
                        state = State.SIGNEDIN;
                        System.out.print(help());
                    } else {
                        System.out.print("Login Failed: Internal Server Failure");
                    }
                } catch (ServerFacadeException e) {
                    System.out.print("Login Failed: " + e.getMessage());
                }
                break;
            case "help":
                System.out.print(help());
                break;
            case "quit":
                return "quit";
            default:
                System.out.print("Unknown Command: Type 'help' for a list of commands");
                break;
        }
        return "";
    }

    private String loggedIn(String input) {
        String[] commands = input.split("\\s+");
        if (commands.length == 0 || commands[0].isEmpty()) {return "";}

        String lower = commands[0].toLowerCase();
        try {
            switch(lower) {
                case "create" -> handleCreate(commands);
                case "list" -> handleList();
                case "help" -> System.out.print(help());
                case "join" -> handleJoin(commands);
                case "spectate" -> handleSpectate(commands);
                case "logout" -> handleLogout();
                case "quit" -> { return "quit"; }
            }
        } catch (ServerFacadeException e) {
            System.out.print("Failed to " + lower + " game: " + e.getMessage());
        }
        return "";
    }

    private void handleCreate(String[] commands) throws ServerFacadeException {
        if (commands.length < 2) {
            System.out.print("Please Enter Fields: create <NAME>");
            return;
        }
        CreateGameResult result = serverFacade.create(commands[1]);
        if (result.gameID() != 0) {
            printColored("Creation Successful!", "MAGENTA");
        } else {
            System.out.print("Creation Failed: Internal Server Error");
        }
    }

    private void handleList() throws ServerFacadeException {
        ListGamesResult result = serverFacade.list();
        if (result == null) {
            System.out.print("No games are currently created... Please create a game!!!");
            return;
        }
        printColored("Current Games \nGame Id | White Player | Black Player | Game Name \n", "GREEN");
        gameList = (ArrayList<GameData>) result.games();
        for (int i = 0; i < gameList.size(); i++) {
            GameData game = gameList.get(i);
            System.out.print("* " + (i + 1) + " " + game.getWhiteUsername() + " " +
                    game.getBlackUsername() + " " + game.getGameName() + "\n");
        }
    }

    private void handleJoin(String[] commands) throws ServerFacadeException {
        if (commands.length < 3) {
            System.out.print("Please Enter Fields: join [WHITE][BLACK] <ID>");
            return;
        }
        int id = parseAndValidateGameId(commands[2]);
        if (id == -1) {return;}

        int gameId = gameList.get(id).getGameID();
        String color = commands[1].toUpperCase();
        serverFacade.join(color, gameId);

        String message = "You have successfully joined a game! You are team " + color +
                "\nBest of luck brave Tarnished\n\n";
        display(new ChessGame(), message, color);
    }

    private void handleSpectate(String[] commands) throws ServerFacadeException {
        if (commands.length < 2) {
            System.out.print("Please Enter Fields: spectate <ID>");
            return;
        }
        int id = parseAndValidateGameId(commands[1]);
        if (id == -1) {return;}

        display(new ChessGame(), "You are now spectating\n\n", "WHITE");
    }

    private void handleLogout() throws ServerFacadeException {
        serverFacade.logout();
        this.authToken = null;
        state = State.SIGNEDOUT;
        printColored("Logout Successful\n", "MAGENTA");
        System.out.println(help());
    }

    private int parseAndValidateGameId(String idStr) {
        int id;
        try {
            id = Integer.parseInt(idStr) - 1;
        } catch (NumberFormatException e) {
            System.out.print("Game ID must be a number.");
            return -1;
        }
        if (gameList == null) {
            System.out.print("Make sure to list games first!");
            return -1;
        }
        if (id < 0 || id >= gameList.size()) {
            System.out.print("Please Enter In The Bounds: 1 ~ " + gameList.size());
            return -1;
        }
        return id;
    }

    private void printColored(String message, String color) {
        String colorCode = color.equals("MAGENTA") ? SET_TEXT_COLOR_MAGENTA : SET_TEXT_COLOR_GREEN;
        System.out.print(SET_TEXT_ITALIC + colorCode + message + RESET_TEXT_ITALIC + RESET_TEXT_COLOR);
    }
    private void printPrompt() {
        if (state.equals(State.SIGNEDIN)) {
            System.out.print("\n" + SET_TEXT_COLOR_MAGENTA + "[LOGGED IN] " + ">>> " + RESET_TEXT_COLOR);
        } else {
            System.out.print("\n" + SET_TEXT_COLOR_MAGENTA + ">>> " + RESET_TEXT_COLOR);
        }
    }

    public void run() {
        System.out.println("♕ Welcome to Kalo's Chess Platform ♕");
        System.out.print("\n" + help());
        Scanner scan = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String input = scan.nextLine();
            if (state.equals(State.SIGNEDIN)) {
                result = loggedIn(input);
            } else {
                result = loggedOut(input);
            }
        }
        System.out.println("Goodbye!");


    }

}
