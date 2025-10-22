package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Server {

    private final Javalin javalin;
    final private HashSet<String> validToken = new HashSet<>(Set.of("token1", "token2"));

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register);

        // Register your endpoints and exception handlers here.

    }
    public void register (Context ctx) {
        ctx.body();
        String authToken = ctx.header("Authorization");
        if (validToken.contains(authToken)) {
            ctx.contentType("application/json");
            ctx.status(403);
            ctx.result(new Gson().toJson(Map.of("msg", "Error: Already Taken")));
        }
    }




    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
