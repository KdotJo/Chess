package client;

import java.util.Scanner;

import exceptions.ServerFacadeException;
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
                - join <ID> [WHITE][BLACK] (Joining a game)
                - spectate <ID> (Spectate a game) 
                - resign (Resign from your game: This means you lose)
                - help (List of commands)
                - logout (Logout of your account)
                """;
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
        switch(lower) {
            case "create":
                if (commands.length < 2) {
                    System.out.print("Please Enter Fields: create <NAME>");
                    return "";
                }
                try {
                    CreateGameResult result = serverFacade.create(commands[1]);
                    if (result.gameID() != 0) {
                        System.out.print(SET_TEXT_ITALIC + SET_TEXT_COLOR_MAGENTA +
                                "Creation Successful! Your game is " + result.gameID() +
                                RESET_TEXT_ITALIC + RESET_TEXT_COLOR);
                    } else {
                        System.out.print("Creation Failed: Internal Server Error");
                    }
                } catch (ServerFacadeException e) {
                    System.out.print("Failed to create game: " + e.getMessage());
                }
                break;
            case "list":
                try {
                    serverFacade.list();
                } catch (ServerFacadeException e) {
                    System.out.print("Failed to list games: " + e.getMessage());
                }
                break;
            case "logout":
                try {
                    serverFacade.logout();
                    this.authToken = null;
                    state = State.SIGNEDOUT;
                    System.out.print(SET_TEXT_ITALIC + SET_TEXT_COLOR_MAGENTA +
                            "Logout Successful" + "\n");
                    return "quit";
                } catch (ServerFacadeException e) {
                    System.out.print("Failed to logout " + e.getMessage());
                }
                break;
        }
        return "";
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
