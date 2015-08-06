package utils

import play.api.libs.ws.DefaultWSClientConfig
import play.api.libs.ws.ning.NingAsyncHttpClientConfigBuilder
import com.ning.http.client.AsyncHttpClientConfig
import play.libs.ws.ning.NingWSClient

object WSUtils {

    def getWSClient = {
        val builder: NingAsyncHttpClientConfigBuilder = new NingAsyncHttpClientConfigBuilder(new DefaultWSClientConfig, new AsyncHttpClientConfig.Builder)
        val httpClientConfig: AsyncHttpClientConfig = builder.build
        new NingWSClient(httpClientConfig)
    }
}
