package simulation;

import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import protocol.Protocol;

public class SimulationSample extends Simulation {

    private final ScenarioBuilder scn = scenario(this.getClass().getSimpleName())
        .exec(http("get user")
            .get("/users/1")
            .check(status().is(200))
        );

    {
        setUp(
            scn.injectOpen(rampUsers(10).during(Duration.of(10, ChronoUnit.MINUTES)))
        ).protocols(Protocol.httpProtocol);
    }
}
