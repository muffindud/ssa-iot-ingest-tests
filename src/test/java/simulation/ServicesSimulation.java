package simulation;

import io.gatling.javaapi.http.HttpProtocolBuilder;
import scenario.SetupScenario;
import scenario.UserAccessScenario;

import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.http;


public class ServicesSimulation extends io.gatling.javaapi.core.Simulation {
    public static final String HTTP_SERVICE_URL = "http://localhost:8080";
    public static final String MQTT_BROKER_URL = "tcp://localhost:1883";

    public static HttpProtocolBuilder httpProtocol = http.baseUrl(HTTP_SERVICE_URL);

    {
        setUp(
                SetupScenario.setup.injectOpen(atOnceUsers(1)).andThen(
                        UserAccessScenario.userAccessScenario.injectOpen(
                                rampUsersPerSec(1).to(10).during(60)
                        )
                )
        ).protocols(httpProtocol);
    }
}
