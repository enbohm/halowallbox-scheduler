package se.enbohms.halo;

import io.quarkus.scheduler.Scheduled;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.JsonbBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class HaloScheduler {

  private static final Logger LOG = Logger.getLogger(HaloScheduler.class);

  private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(Version.HTTP_2)
      .connectTimeout(Duration.ofSeconds(10)).build();
  
  private final AtomicBoolean isLedOn = new AtomicBoolean(false);

  @ConfigProperty(name = "halo.auth.endpoint")
  String haloAuthEndpoint;
  @ConfigProperty(name = "halo.settings.endpoint")
  String haloSettingsEndpoint;
  @ConfigProperty(name = "halo.apikey")
  String apiKey;
  @ConfigProperty(name = "halo.username")
  String userName;
  @ConfigProperty(name = "halo.pwd")
  String pwd;

  @Scheduled(every = "60s")
  void scheduleHaloOnOff() throws Exception {
    LocalTime now = LocalTime.now();
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
        .POST(HttpRequest.BodyPublishers.ofString(jsonAuth)).uri(URI.create(haloAuthEndpoint))
        .setHeader("apiKey", apiKey).header("Content-Type", "application/json").build();

    HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

    return JsonbBuilder.create().fromJson(response.body(), HaloAuthResponse.class);
  }

  private void turnLightOn(HaloAuthResponse authResponse)
      throws java.io.IOException, InterruptedException {
    String jsonEnableLedLight = new StringBuilder().append("{").append("\"id\":\"2005010779M\",")
        .append("\"dimmer\":\"High\",").append("\"downlight\":\"false\"").
            append("}").toString();

    sendPutRequest(authResponse, jsonEnableLedLight);
    LOG.info("TURNING ON LED");
  }

  private void turnLightOff(HaloAuthResponse authResponse)
      throws java.io.IOException, InterruptedException {
    String jsonDisableLedLight = new StringBuilder().append("{").append("\"id\":\"2005010779M\",")
        .append("\"dimmer\":\"OFF\",").append("\"downlight\":\"false\"").
            append("}").toString();

    sendPutRequest(authResponse, jsonDisableLedLight);
    LOG.info("TURNING OFF LED");
  }

  private void sendPutRequest(HaloAuthResponse authResponse, String jsonPut)
      throws java.io.IOException, InterruptedException {
    HttpRequest putRequest = HttpRequest.newBuilder()
        .PUT(HttpRequest.BodyPublishers.ofString(jsonPut)).uri(URI.create(haloSettingsEndpoint))
        .setHeader("Authorization", "Bearer " + authResponse.getToken())
        .header("Content-Type", "application/json").build();

    HttpResponse<String> putResponse = HTTP_CLIENT
        .send(putRequest, HttpResponse.BodyHandlers.ofString());
    LOG.info("PUT status code " + putResponse.statusCode());
    LOG.info("PUT response body " + putResponse.body());
  }
}

