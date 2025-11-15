package scenario;

import io.gatling.javaapi.core.Body;
import io.gatling.javaapi.core.ScenarioBuilder;
import utils.DataHolder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.mqtt.MqttDsl.*;

public class IotIngestScenario {
    private static final String MQTT_TOPIC = "iot/ingest";
    private static final Body MQTT_BODY = StringBody("{\"token\": \"${deviceToken}\", \"data\": \"{\"temperature\": 15.6}\" }");
    private static final Body HTTP_BODY = StringBody("{\"temperature\": 15.6}\"");

    public static ScenarioBuilder mqttScenario = scenario("IOT MQTT Publish")
            .exec(session -> session.set("deviceToken", DataHolder.data.get("deviceToken")))
            .exec(mqtt("Connect MQTT Client").connect())
            .exec(mqtt("Publish")
                    .publish(MQTT_TOPIC)
                    .message(MQTT_BODY)
            );

    public static ScenarioBuilder httpScenario = scenario("Http IOT Ingest" )
            .exec(session -> session.set("deviceToken", DataHolder.data.get("deviceToken")))
            .exec(http("HTTP IOT Ingest")
                    .post("http://localhost:8080/iot/ingest/publish")
                    .header("Authorization", "Bearer ${deviceToken}")
                    .body(HTTP_BODY).asJson()
            );
}
