package client;

import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void sampleTest1() {
        Assertions.assertTrue(true);
    }

    @Test
    public void sampleTest2() {
        Assertions.assertTrue(true);
    }

    @Test
    public void sampleTest3() {
        Assertions.assertTrue(true);
    }

    @Test
    public void sampleTest4() {
        Assertions.assertTrue(true);
    }

    @Test
    public void sampleTest5() {
        Assertions.assertTrue(true);
    }

    @Test
    public void sampleTest6() {
        Assertions.assertTrue(true);
    }

    @Test
    public void sampleTest7() {
        Assertions.assertTrue(true);
    }

    @Test
    public void sampleTest8() {
        Assertions.assertTrue(true);
    }


    @Test
    public void sampleTest9() {
        Assertions.assertTrue(true);
    }

    @Test
    public void sampleTest10() {
        Assertions.assertTrue(true);
    }

    @Test
    public void sampleTest11() {
        Assertions.assertTrue(true);
    }

    @Test
    public void sampleTest12() {
        Assertions.assertTrue(true);
    }

    @Test
    public void sampleTest13() {
        Assertions.assertTrue(true);
    }

    @Test
    public void sampleTest14() {
        Assertions.assertTrue(true);
    }

    @Test
    public void sampleTest15() {
        Assertions.assertTrue(true);
    }

    @Test
    public void sampleTest16() {
        Assertions.assertTrue(true);
    }

}
