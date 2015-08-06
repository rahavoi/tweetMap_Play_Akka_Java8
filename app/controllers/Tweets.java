package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public class Tweets extends Controller {

    public static Result index() {
        return ok(views.html.index.render("TweetMap"));
    }
    public static Promise<Result> search(String query){
        return fetchTweets(query)
                .map(jsonNode -> ok(jsonNode));
    }

    public static Promise<JsonNode> fetchTweets(String query){
        Promise<WSResponse> responsePromise = WS.url("http://twitter-search-proxy.herokuapp.com/search/tweets").setQueryParameter("q", query).get();

        return responsePromise
                .filter(response -> response.getStatus() == Http.Status.OK)
                .map(response -> response.asJson())
                .recover(Tweets::errorResponse);
    }


    public static JsonNode errorResponse(Throwable ignored){
        return Json.newObject().put("error", "Could not fetch the tweets");
    }



}
