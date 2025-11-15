package simulation;

import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import scenario.IotIngestScenario;
import scenario.SetupScenario;
import scenario.UserAccessScenario;
import utils.Config;

import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.OpenInjectionStep.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class MonolithSimulation extends Simulation {
    public static final String HTTP_SERVICE_URL = "http://localhost:8080";

    public static HttpProtocolBuilder httpProtocol = http.baseUrl(HTTP_SERVICE_URL);

    {
        setUp(
                SetupScenario.setup.injectOpen(atOnceUsers(1)).andThen(
                        IotIngestScenario.httpScenario.injectOpen(
                                atOnceUsers(5)
                        ),
                        UserAccessScenario.userAccessScenario.injectOpen(
                                rampUsersPerSec(Config.START_USER_COUNT).to(Config.MAX_USER_COUNT).during(Config.DURATION_SECONDS)
                        ),
                        SetupScenario.setup.injectOpen(
                                rampUsersPerSec(Config.START_USER_COUNT).to(Config.MAX_USER_COUNT).during(Config.DURATION_SECONDS)
                        )
                )
        ).protocols(httpProtocol);
    }
}
