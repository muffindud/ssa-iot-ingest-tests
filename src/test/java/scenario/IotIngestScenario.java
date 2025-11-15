package scenario;

import io.gatling.javaapi.core.Body;
import io.gatling.javaapi.core.ScenarioBuilder;
import utils.Config;
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
    private static final Body HTTP_BODY = StringBody("{\"temperature\": 15.6}");

    public static ScenarioBuilder mqttScenario = scenario("IOT MQTT Publish")
            .exec(mqtt("Connect MQTT Client").connect())
            .repeat(Config.PUBLISH_REPEAT_COUNT).on(exec(mqtt("Publish")
                            .publish(MQTT_TOPIC)
                            .message(MQTT_BODY)
                    ).pause(Duration.ofSeconds(Config.PUBLISH_DELAY_SECONDS))
            );

    public static ScenarioBuilder httpScenario = scenario("Http IOT Ingest" )
            .exec(session -> session.set("deviceToken", DataHolder.data.get("deviceToken")))
            .repeat(Config.PUBLISH_REPEAT_COUNT).on(exec(http("HTTP IOT Ingest")
                            .post("/iot/ingest/publish")
                            .header("Content-Type", "application/json")
                            .header("Authorization", session -> {
                                String deviceToken = DataHolder.listData.get("deviceTokens").get(0).toString();
                                return "Bearer " + deviceToken;
                            })
                            .body(HTTP_BODY)
                    ).pause(Duration.ofSeconds(Config.PUBLISH_DELAY_SECONDS))
            );
}
