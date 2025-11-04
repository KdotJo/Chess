package service;

import dataaccess.DataAccessException;
import dataaccess.mysql.MySqlUserDao;
import model.UserData;
import org.junit.jupiter.api.Test;
import server.Server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AuthDaoTests {
    @Test
    public void testCreateNewGame() {
        Server server = new Server();
    }

    @Test
    public void testFailedCreateGame() {
        Server server = new Server();
    }
}
