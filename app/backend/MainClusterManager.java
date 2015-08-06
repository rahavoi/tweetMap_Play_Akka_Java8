package backend;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainClusterManager {

    public static void main(String... args) throws IOException {

        ActorSystem system = startSystem(args);

        init(system);

        commandLoop(system);
    }

    private static void init(ActorSystem system) {
        // replace this with the code to create backend actors.
        // system.actorOf(SomeActor.props(), "someActor");
    }

    private static ActorSystem startSystem(String... cmdArgs) {
        if (cmdArgs.length < 1) {
            return startSystem("backend");
        } else {
            String role = cmdArgs[0];
            String port = cmdArgs[1];
            return startSystem(role, port);
        }
    }

    public static ActorSystem startSystem(String role, String port) {
        int portNr = Integer.parseInt(port);

        // Override the port number and role configuration
        Config config = ConfigFactory.parseString("akka.cluster.roles=[" + role + "]").
                withFallback(ConfigFactory.parseString("akka.loglevel=INFO")).
                withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=" + portNr)).
                withFallback(ConfigFactory.load());

        return ActorSystem.create("application", config);
    }

    public static void commandLoop(ActorSystem system) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter `s` to shutdown:");
        String s = br.readLine();
        if (s.startsWith("s")) {
            system.shutdown();

        } else {
            commandLoop(system);
        }
    }
}
