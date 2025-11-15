package scenario;

import io.gatling.javaapi.core.Body;
import io.gatling.javaapi.core.ScenarioBuilder;
import utils.DataHolder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.mqtt.MqttDsl.*;

public class IotIngestScenario {
    private static final String MQTT_TOPIC = "iot/ingest";
    private static final Body MQTT_BODY = StringBody(session -> {
        String deviceToken = (String) DataHolder.listData.get("deviceTokens").get(0);
        return String.format("{\"token\": \"%s\", \"data\": {\"temperature\": 15.6} }", deviceToken);
    });
    private static final Body HTTP_BODY = StringBody("{\"temperature\": 15.6}\"");

    public static ScenarioBuilder mqttScenario = scenario("IOT MQTT Publish")
            .exec(mqtt("Connect MQTT Client").connect())
            .repeat(20).on(exec(mqtt("Publish")
                            .publish(MQTT_TOPIC)
                            .message(MQTT_BODY)
                    ).pause(Duration.ofSeconds(3))
            );

    public static ScenarioBuilder httpScenario = scenario("Http IOT Ingest" )
            .exec(session -> session.set("deviceToken", DataHolder.data.get("deviceToken")))
            .repeat(20).on(exec(http("HTTP IOT Ingest")
                            .post("/iot/ingest/publish")
                            .header("Authorization", session -> {
                                String deviceToken = DataHolder.listData.get("deviceTokens").get(0).toString();
                                return "Bearer " + deviceToken;
                            })
                            .body(HTTP_BODY)
                    ).pause(Duration.ofSeconds(3))
            );
}
