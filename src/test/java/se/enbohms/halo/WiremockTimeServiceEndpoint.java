package se.enbohms.halo;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Collections;
import java.util.Map;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WiremockTimeServiceEndpoint implements QuarkusTestResourceLifecycleManager {

  private WireMockServer wireMockServer;

  @Override
  public Map<String, String> start() {
    wireMockServer = new WireMockServer();
    wireMockServer.start();

    stubFor(get(urlEqualTo("/json?lat=56.170172&lng=14.863128&formatted=0"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(
                "{\"results\": {" +
                    "\"sunrise\": \"2021-03-30T06:05:12+00:00\"," +
                    "\"sunset\": \"2021-03-30T18:38:38+00:00\"" +
                    "}, \"status\":\"OK\"}"
            )));


    return Map.of("sunset-api/mp-rest/url", wireMockServer.baseUrl());
  }

  @Override
  public void stop() {
    if (null != wireMockServer) {
      wireMockServer.stop();
    }
  }
}


