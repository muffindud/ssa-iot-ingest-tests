package scenario;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import utils.DataHolder;
import utils.RandomUtils;

import java.time.Duration;
import java.util.ArrayList;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.http;

public class SetupScenario {
    private static final ChainBuilder createAndLoginUser =
            exec(session -> {
                DataHolder.data.put("username", "user_" + RandomUtils.generateRandomMacAddress());
                DataHolder.data.put("password", "Password123!");
                return session;
            })
            .exec(http("Register User")
                    .post("/user/auth/register")
                    .header("Content-Type", "application/json")
                    .body(StringBody(session -> {
                        String username = DataHolder.getData("username");
                        String password = DataHolder.getData("password");
                        return String.format("{ \"username\": \"%s\", \"password\": \"%s\" }", username, password);
                    }))
            ).pause(Duration.ofSeconds(2))
            .exec(http("Login User")
                    .get("/user/auth/login")
                    .header("Content-Type", "application/json")
                    .body(StringBody(session -> {
                        String username = DataHolder.getData("username");
                        String password = DataHolder.getData("password");
                        return String.format("{ \"username\": \"%s\", \"password\": \"%s\" }", username, password);
                    }))
                    .check(jsonPath("$.access_token").saveAs("accessToken"))
                    .check(jsonPath("$.refresh_token").saveAs("refreshToken"))
            ).pause(Duration.ofSeconds(2))
            .exec(session -> {
                DataHolder.data.put("accessToken", session.getString("accessToken"));
                DataHolder.data.put("refreshToken", session.getString("refreshToken"));
                return session;
            });

    private static final ChainBuilder createAndLinkDevice =
            exec(session -> {
                DataHolder.data.put("deviceMac", RandomUtils.generateRandomMacAddress());
                DataHolder.data.put("deviceType", "general");
                return session;
            })
            .exec(http("Register Device")
                    .post("/iot/auth/register")
                    .header("Content-Type", "application/json")
                    .body(StringBody(session -> {
                        String deviceMac = DataHolder.getData("deviceMac");
                        String deviceType = DataHolder.getData("deviceType");
                        return String.format("{ \"mac\": \"%s\", \"type\": \"%s\" }", deviceMac, deviceType);
                    }))
                    .check(jsonPath("$.access_token").saveAs("deviceAccessToken"))
                    .check(jsonPath("$.refresh_token").saveAs("deviceRefreshToken"))
            ).pause(Duration.ofSeconds(1))
            .exec(http("Link Device")
                    .post("/user/auth/link")
                    .header("Content-Type", "application/json")
                    .header("Authorization", session -> "Bearer " + DataHolder.getData("accessToken"))
                    .body(StringBody(session -> {
                        String deviceMac = DataHolder.getData("deviceMac");
                        return String.format("{ \"mac\": \"%s\" }", deviceMac);
                    }))
            ).pause(Duration.ofSeconds(1))
            .exec(session -> {
                DataHolder.addToListData("deviceTokens", session.getString("deviceAccessToken"));
                return session;
            }).pause(Duration.ofSeconds(1));

    public static ScenarioBuilder setup = scenario("Monolith Simulation")
            .exec(createAndLoginUser)
            .exec(createAndLinkDevice)
            .exec(createAndLinkDevice)
            .exec(createAndLinkDevice);
}
