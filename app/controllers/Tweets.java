package controllers;

import actors.UserActor;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;

public class Tweets extends Controller {

    public static Result index() {
        return ok(views.html.index.render("TweetMap"));
    }

    /**
     * A reactive request is made in Play by returning a Promise of a result.  This makes the request asynchronous
     * since the server doesn't block waiting for the response.  This frees the server thread to handle other requests.
     * The callback to the Promise is added as a map, which maps the results to a new value when the Promise completes.
     * The results of the Promise in this example are mapped to a result (HTTP 200 OK) that gets returned to the client.
     **/
    public static Promise<Result> search(String query){
        return fetchTweets(query)
                .map(jsonNode -> ok(jsonNode));
    }

    /**
     * Fetch the latest tweets and return the Promise of the json results.
     * This fetches the tweets asynchronously and fulfills the promise when the results are returned.
     * The results are first filtered and only returned if the result status was OK.
     * Then the results are mapped (or transformed) to JSON.
     * Finally a recover is added to the Promise to return an error Json if the tweets couldn't be fetched.
     *
     * @param query
     * @return
     */
    public static Promise<JsonNode> fetchTweets(String query){
        Promise<WSResponse> responsePromise = WS.url("http://twitter-search-proxy.herokuapp.com/search/tweets").setQueryParameter("q", query).get();

        return responsePromise
                .filter(response -> response.getStatus() == Http.Status.OK)
                .map(response -> response.asJson())
                .recover(Tweets::errorResponse);
    }

    /**
     * The error response when the twitter search fails.
     *
     * @param ignored
     * @return
     */
    public static JsonNode errorResponse(Throwable ignored){
        return Json.newObject().put("error", "Could not fetch the tweets");
    }

    public static WebSocket<JsonNode> ws(){
        return WebSocket.withActor(UserActor :: props);
    }




}
