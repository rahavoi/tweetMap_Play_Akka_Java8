package actors;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import controllers.Tweets;
import scala.concurrent.duration.Duration;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class UserActor extends AbstractActor {

    /**
     * The query to search for - empty if a query has not been sent from the user
     */
    public Optional<String> optQuery = Optional.empty();

    /**
     * Creates a new UserActor using these Props.
     *
     * @param out
     * @return
     */
    public static Props props(ActorRef out) {
        return Props.create(UserActor.class, out);
    }

    /**
     * The out ActorRef is used to write back out to the websocket client
     * It is created by Play and set when the UserActor is created.
     */
    private final ActorRef out;

    /**
     * Construct the UserActor and initialize the receive block.
     * The receive block defines this actor handles.
     *
     * @param out
     */
    public UserActor(ActorRef out) {
        this.out = out;

        receive(ReceiveBuilder.
                        //A json message is from the client so parse it to get the query and fetch the tweets.
                                match(JsonNode.class, jsonNode -> {
                            String query = jsonNode.findPath("query").textValue();
                            optQuery = Optional.of(query);
                            runFetchTweets(query);
                        }).
                        //The Update message is sent from the scheduler.  When the Actor receives the
                                //message fetch the tweets only if there is a query from the user.
                                match(Update.class, update -> optQuery.ifPresent(this::runFetchTweets)).
                        matchAny(o -> System.out.println("received unknown message")).build()
        );
    }

    /**
     * Fetch the latest tweets for a given query and send the results to
     * the out actor - which in turns sends it back up to the client via a websocket.
     *
     * @param query
     */
    private void runFetchTweets(String query) {
        Tweets.fetchTweets(query).onRedeem(json -> {
            out.tell(json, self());
        });
    }


    /**
     * The Update class is used to send a message to this actor to
     * re-run the query and send the results to the client.
     */
    public static final class Update {
    }

    private final ActorSystem system = getContext().system();

    //This will schedule to send the Update message
    //to this actor after 0ms repeating every 5s.  This will cause this actor to search for new tweets every 5 seconds.
    Cancellable cancellable = system.scheduler().schedule(Duration.Zero(),
            Duration.create(5, TimeUnit.SECONDS), self(), new Update(),
            system.dispatcher(), null);

}