package client;

import java.util.Scanner;
import client.ServerFacade;
import exceptions.ServerFacadeException;
import request.LoginRequest;
import result.LoginResult;
import result.RegisterResult;

import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade serverFacade;
    private State state = State.SIGNEDOUT;

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

    private String handleInput(String input) {
        String[] inputs = input.split("\\s+");
        if (inputs.length == 0 || inputs[0].isEmpty()) {return "";}
        String lower = inputs[0].toLowerCase();
        switch (lower) {
            case "register":
                if (inputs.length < 4) {
                    System.out.println("Please Enter Fields: register <USERNAME> <PASSWORD> <EMAIL>");
                    return "";
                }
                try {
                    RegisterResult result = serverFacade.register(inputs[1], inputs[2], inputs[3]);
                    if (result.authToken() != null) {
                        System.out.println("Register Successful! Welcome " + result.username());
                        state = State.SIGNEDIN;
                    } else {
                        System.out.println("Registration Failed: Internal Server Failure");
                    }
                } catch (ServerFacadeException e) {
                    System.out.println("Registration Failed: " + e.getMessage());
                }
                break;
            case "login":
                if (inputs.length < 3) {
                    System.out.println("Please Enter Fields: login <USERNAME> <PASSWORD>");
                    return "";
                }
                try {
                    LoginResult result = serverFacade.login(inputs[1], inputs[2]);
                    if (result.authToken() != null) {
                        System.out.println("Login Successful! Welcome " + result.username());
                        state = State.SIGNEDIN;
                    } else {
                        System.out.println("Login Failed: Internal Server Failure");
                    }
                } catch (ServerFacadeException e) {
                    System.out.println("Login Failed: " + e.getMessage());
                }
                break;
            case "help":
                System.out.println(help());
                break;
            case "quit":
                return "quit";
            default:
                System.out.println("Unknown Command: Type 'help' for a list of commands");
                break;
        }
        return "";
    }

    private void printPrompt() {
        if (state.equals(State.SIGNEDIN)) {
            System.out.print("\n" + SET_TEXT_COLOR_MAGENTA + "[LOGGED IN]" + ">>> " + RESET_TEXT_COLOR);
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
            result = handleInput(input);
        }
        System.out.println("Goodbye!");


    }

}
