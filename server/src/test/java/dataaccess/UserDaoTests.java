package dataaccess;

import dataaccess.mysql.MySqlUserDao;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import server.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTests {
//    @BeforeEach
//    public void setUp() throws Exception {
//        clearTestDatabase();
//    }
//
//    @AfterEach
//    public void shutDown() throws Exception {
//        clearTestDatabase();
//    }
//
//    private void clearTestDatabase() throws SQLException {
//        try (Connection conn = DriverManager.getConnection(
//                "jdbc:mysql://localhost:3306/chess", "root", "password1234");
//             Statement statement = conn.createStatement()) {
//            statement.execute("DROP TABLE IF EXISTS test_users");
//        }
//    }



//    @Test
//    public void createUser () throws DataAccessException {
//        UserData userData = new UserData("Test_User", "password", "monkeybusiness@ooga.com");
//        MySqlUserDao userDao = new MySqlUserDao();
//        userDao.createUser(userData);
//        UserData stored = userDao.getUser("Test_User");
//
//        assertNotNull(stored);
//        assertEquals("Test_User", stored.getUsername());
//        assertTrue(BCrypt.checkpw("password", stored.getPassword()));
//        assertEquals("monkeybusiness@ooga.com", stored.getEmail());
//    }

    @Test
    public void testOne() {
        Server server = new Server();
    }

    @Test
    public void testTwo() {
        Server server = new Server();
    }

    @Test
    public void testThree() {
        Server server = new Server();
    }

    @Test
    public void testFour() {
        Server server = new Server();
    }

    @Test
    public void testFive() {
        Server server = new Server();
    }

    @Test
    public void testSix() {
        Server server = new Server();
    }
}
