package se.enbohms.halo;

import io.quarkus.scheduler.Scheduled;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.JsonbBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class HaloScheduler {

  private static final String HALO_SETTING_ENDPOINT =
      "https://eapi.charge" + ".space/api/v3/chargepoints/2005010779M/settings";
  private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(Version.HTTP_2)
      .connectTimeout(Duration.ofSeconds(10)).build();
  private static final String HALO_AUTH_ENDPOINT = "https://eapi.charge.space/api/v3/auth/login";
  private final AtomicBoolean isLedOn = new AtomicBoolean(false);

  @ConfigProperty(name = "halo.apikey")
  String apiKey;
  @ConfigProperty(name = "halo.username")
  String userName;
  @ConfigProperty(name = "halo.pwd")
  String pwd;

  @Scheduled(cron = "0 15 10 * * ?")
  void fireAt10AmEveryDay() {
    System.out.println("****");
  }

  @Scheduled(every = "60s")
  void scheduleHaloOnOff() throws Exception {
    LocalTime now = LocalTime.now();
    System.out.println(" Api Key " + apiKey);
    if (now.isAfter(LocalTime.of(20, 00)) && now.isBefore(LocalTime.of(23, 00)) && !isLedOn.get()) {
      turnLightOn(getAuthToken());
      isLedOn.set(true);
    } else if (now.isAfter(LocalTime.of(23, 00)) && isLedOn.get()) {
      turnLightOff(getAuthToken());
      isLedOn.set(false);
    }
  }

  private HaloAuthResponse getAuthToken() throws java.io.IOException, InterruptedException {
    String jsonAuth = new StringBuilder().append("{").append("\"email\":\"" + userName + "\",")
        .append("\"password\":\"" + pwd + "\"").append("}").toString();
    HttpRequest request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(jsonAuth)).uri(URI.create(HALO_AUTH_ENDPOINT))
        .setHeader("apiKey", apiKey).header("Content-Type", "application/json").build();

    HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

    HaloAuthResponse authResponse = JsonbBuilder.create()
        .fromJson(response.body(), HaloAuthResponse.class);
    return authResponse;
  }

  private void turnLightOn(HaloAuthResponse authResponse)
      throws java.io.IOException, InterruptedException {
    String jsonEnableLedLight = new StringBuilder().append("{").append("\"id\":\"2005010779M\",")
        .append("\"dimmer\":\"High\",").append("\"downlight\":\"false\"").
            append("}").toString();

    sendPutRequest(authResponse, jsonEnableLedLight);
    System.out.println("TURNING ON LED");
  }

  private void turnLightOff(HaloAuthResponse authResponse)
      throws java.io.IOException, InterruptedException {
    String jsonDisableLedLight = new StringBuilder().append("{").append("\"id\":\"2005010779M\",")
        .append("\"dimmer\":\"OFF\",").append("\"downlight\":\"false\"").
            append("}").toString();

    sendPutRequest(authResponse, jsonDisableLedLight);
    System.out.println("TURNING OFF LED");
  }

  private void sendPutRequest(HaloAuthResponse authResponse, String jsonPut)
      throws java.io.IOException, InterruptedException {
    HttpRequest putRequest = HttpRequest.newBuilder()
        .PUT(HttpRequest.BodyPublishers.ofString(jsonPut)).uri(URI.create(HALO_SETTING_ENDPOINT))
        .setHeader("Authorization", "Bearer " + authResponse.getToken())
        .header("Content-Type", "application/json").build();

    HttpResponse<String> putResponse = HTTP_CLIENT
        .send(putRequest, HttpResponse.BodyHandlers.ofString());
    System.out.println("PUT status code " + putResponse.statusCode());
    System.out.println("PUT response body " + putResponse.body());
  }
}

