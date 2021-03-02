package se.enbohms.halo;

import io.quarkus.scheduler.Scheduled;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
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
  @ConfigProperty(name = "halo.start.hour")
  int startHour;
  @ConfigProperty(name = "halo.start.minute")
  int startMinute;

  @PostConstruct
  public void init() {
    LOG.info("Initializing Halo Scheduler using API-key " + apiKey);
  }

  @Scheduled(every = "60s")
  void scheduleHaloOnOff() throws Exception {
    LocalTime now = LocalTime.now();
    if (now.isAfter(LocalTime.of(startHour, startMinute)) && now.isBefore(LocalTime.of(23, 00))
        && !isLedOn.get()) {
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

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(URI.create(haloAuthEndpoint));
    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    headers.add("apiKey", apiKey);
    headers.add("Content-Type", MediaType.APPLICATION_JSON);

    Response response = target.request(MediaType.WILDCARD).headers(headers)
        .post(Entity.json(jsonAuth));
    return response.readEntity(HaloAuthResponse.class);
  }

  private void turnLightOn(HaloAuthResponse authResponse) {
    String jsonEnableLedLight = new StringBuilder().append("{").append("\"id\":\"2005010779M\",")
        .append("\"dimmer\":\"Medium\",").append("\"downlight\":\"false\"").
            append("}").toString();

    sendPutRequest(authResponse, jsonEnableLedLight);
    LOG.info("TURNING ON LED");
  }

  private void turnLightOff(HaloAuthResponse authResponse) {
    String jsonDisableLedLight = new StringBuilder().append("{").append("\"id\":\"2005010779M\",")
        .append("\"dimmer\":\"OFF\",").append("\"downlight\":\"false\"").
            append("}").toString();

    sendPutRequest(authResponse, jsonDisableLedLight);
    LOG.info("TURNING OFF LED");
  }

  private void sendPutRequest(HaloAuthResponse authResponse, String jsonPayload) {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(URI.create(haloSettingsEndpoint));
    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    headers.add("Authorization", "Bearer " + authResponse.getToken());
    headers.add("Content-Type", MediaType.APPLICATION_JSON);

    Response putResponse = target.request(MediaType.APPLICATION_JSON).headers(headers)
        .put(Entity.json(jsonPayload));

    LOG.info("PUT status code " + putResponse.getStatus());
    LOG.info("PUT response body " + putResponse.readEntity(String.class));
  }
}

