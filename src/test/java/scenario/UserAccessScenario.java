package scenario;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import utils.DataHolder;

import java.time.Duration;
import java.util.List;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class UserAccessScenario {
    private static final ChainBuilder getDevices =
            exec(http("Get User Devices")
                    .get("/user/access/devices")
                    .header("Content-Type", "application/json")
                    .header("Authorization", session -> "Bearer " + DataHolder.getData("accessToken"))
                    .check(jsonPath("$.device_ids[*]").findAll().saveAs("deviceIds"))
            ).pause(Duration.ofSeconds(1))
            .exec(session -> {
                DataHolder.listData.put("deviceIds", session.getList("deviceIds"));
                return session;
            });

    private static final ChainBuilder retrieveDeviceData =
            exec(http("Retrieve Device Data")
                    .get("/user/access/retrieve")
                    .header("Content-Type", "application/json")
                    .header("Authorization", session -> "Bearer " + DataHolder.getData("accessToken"))
                    .body(StringBody(session -> {
                        List<Object> deviceIds = DataHolder.listData.get("deviceIds");
                        if (deviceIds.isEmpty()) {
                            return "{ \"device_id\": null, \"size\": 10, \"page\": 1 }";
                        } else {
                            return String.format("{ \"device_id\": %s, \"size\": 10, \"page\": 1 }", deviceIds.get(0));
                        }
                    }))
                    .asJson()
            ).pause(Duration.ofSeconds(1));

    public static ScenarioBuilder userAccessScenario = scenario("User Access Scenario")
            .exec(getDevices)
            .exec(retrieveDeviceData);
}
