package simulation;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import static protocol.Protocol.httpProtocol;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import net.datafaker.Faker;

public class TeumTeumApiSimulation extends Simulation {

    private static final Faker faker = new Faker();
    private final ScenarioBuilder teumteumScn = scenario("TeumTeum 찬해지기 API 부하 테스트를 진행한다.")
        .exec(session ->
            session
                .set("id", java.util.UUID.randomUUID().toString())
                .set("name", faker.name().fullName()))

        .exec(http("User 카드 등록 API 요청")
            .post("/users")
            .body(StringBody(
                "{"
                    + "\"id\": \"${id}\", "
                    + "\"terms\": {\"service\": true, \"privatePolicy\": true}, "
                    + "\"name\": \"${name}\", "
                    + "\"birth\": \"20000402\", "
                    + "\"characterId\": 2, "
                    + "\"authenticated\": \"네이버\", "
                    + "\"activityArea\": \"경기 시흥\", "
                    + "\"mbti\": \"ENFP\", "
                    + "\"status\": \"직장인\", "
                    + "\"job\": {\"name\" : \"카카오 뱅크\", \"class\" : \"개발\", \"detailClass\" : \"BE 개발자\"}, "
                    + "\"interests\": [\"네트워킹\", \"IT\", \"모여서 각자 일하기\"], "
                    + "\"goal\": \"회사에서 좋은 사람들과 멋진 개발하기\""
                    + "}"
            ))
            .check(status().is(201))
            .check(jsonPath("$.id").saveAs("userId"))
            .check(jsonPath("$.accessToken").saveAs("accessToken"))
            .check(jsonPath("$.refreshToken").saveAs("refreshToken")))

        .exec(http("TeumTeum 친해지기 API 요청")
            .post("/teum-teum/around")
            .header("Authorization", "Bearer ${accessToken}")
            .body(StringBody("{\"id\": ${userId}, \"latitude\": 37.5665, \"longitude\": 126.9780,"
                + " \"name\": \"test_name\", \"jobDetailClass\": \"test_job\", \"characterId\": 1}"))
            .check(status().is(200))
        );

    {
        setUp(
            teumteumScn.injectOpen(
                constantUsersPerSec(10).during(Duration.of(30, ChronoUnit.SECONDS)),
                rampUsersPerSec(10).to(50).during(Duration.of(30, ChronoUnit.SECONDS))
            ).protocols(httpProtocol)
        );
    }
}
