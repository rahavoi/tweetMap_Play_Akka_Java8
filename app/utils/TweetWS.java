package utils;

import play.libs.ws.WSRequestHolder;
import play.libs.ws.ning.NingWSClient;

public class TweetWS {
    public static NingWSClient wsClient = WSUtils.getWSClient();
    public static WSRequestHolder url(String url) {
        return wsClient.url(url);
    }
}
