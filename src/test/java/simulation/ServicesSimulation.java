package simulation;

import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.mqtt.MqttProtocolBuilder;
import scenario.IotIngestScenario;
import scenario.SetupScenario;
import scenario.UserAccessScenario;

import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.mqtt.MqttDsl.mqtt;


public class ServicesSimulation extends io.gatling.javaapi.core.Simulation {
    public static final String HTTP_SERVICE_URL = "http://localhost:8080";
    public static final String MQTT_BROKER_HOST = "localhost";
    public static final int MQTT_BROKER_PORT = 1883;

    public static HttpProtocolBuilder httpProtocol = http.baseUrl(HTTP_SERVICE_URL);
    public static MqttProtocolBuilder mqttProtocol = mqtt.broker(MQTT_BROKER_HOST, MQTT_BROKER_PORT);

    {
        setUp(
                SetupScenario.setup.injectOpen(atOnceUsers(1)).andThen(
                        IotIngestScenario.mqttScenario.injectOpen(
                                atOnceUsers(5)
                        ).protocols(),
                        UserAccessScenario.userAccessScenario.injectOpen(
                                rampUsersPerSec(1).to(10).during(60)
                        )
                )
        ).protocols(httpProtocol);
    }
}
