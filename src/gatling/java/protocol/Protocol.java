package protocol;

import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class Protocol {

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0";

    public static final HttpProtocolBuilder httpProtocol = HttpDsl.http.baseUrl("https://api.teum.org")
        .header("Content-Type", "application/json")
        .userAgentHeader(USER_AGENT);

    private Protocol() {
        throw new UnsupportedOperationException("Cannot invoke constructor \"protocol.Protocol()\"");
    }
}
