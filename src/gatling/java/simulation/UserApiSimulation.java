package simulation;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import static protocol.Protocol.httpProtocol;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;

public class UserApiSimulation extends Simulation {

    private final ScenarioBuilder UserScn = scenario("User API 부하 테스트를 진행한다.")
        .exec(http("User 카드 등록 API 요청")
            .post("/users")
            .body(StringBody(
                "{\"id\": \"test_id\", "
                    + "\"terms\": {\"service\": true, \"privatePolicy\": true}, "
                    + "\"name\": \"홍길동\", "
                    + "\"birth\": \"1990-01-01\", "
                    + "\"characterId\": 1, "
                    + "\"authenticated\": \"SNS\", "
                    + "\"activityArea\": \"서울\", "
                    + "\"mbti\": \"INTJ\", "
                    + "\"status\": \"ACTIVE\", "
                    + "\"job\": {\"name\": \"개발자\", \"class\": \"IT\", \"detailClass\": \"백엔드\"}, "
                    + "\"interests\": [\"코딩\", \"독서\", \"운동\"], "
                    + "\"goal\": \"성장하기 위해 노력하는 개발자가 되기\"}"
            ))
            .check(status().is(201))
            .check(jsonPath("$.id").saveAs("userId"))
            .check(jsonPath("$.accessToken").saveAs("accessToken"))
            .check(jsonPath("$.refreshToken").saveAs("refreshToken"))

        ).exec(http("User 정보 조회 API 요청")
            .get("/users/${userId}")
            .header("Authorization", "Bearer ${accessToken}")
            .check(status().is(200))

        ).exec(http("User 리뷰 조회 API 요청")
            .get("/users/${userId}")
            .header("Authorization", "Bearer ${accessToken}")
            .check(status().is(200)));


    {
        setUp(
            UserScn.injectOpen(
                atOnceUsers(10)).protocols(httpProtocol));
    }
}
