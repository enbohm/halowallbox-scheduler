package se.enbohms.halo;

import java.net.URI;
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
public class HaloService {

  private static final Logger LOG = Logger.getLogger(HaloService.class);

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

  @PostConstruct
  public void init() {
    LOG.info("Initializing Halo Service using API-key " + apiKey);
  }

  public void turnLightOn() {
    HaloAuthResponse haloAuthResponse = getAuthToken();
    String jsonEnableLedLight = new StringBuilder().append("{").append("\"id\":\"2005010779M\",")
        .append("\"dimmer\":\"Medium\",").append("\"downlight\":\"false\"").
            append("}").toString();

    sendPutRequest(haloAuthResponse, jsonEnableLedLight);
    LOG.info("TURNING ON LED");
  }

  public void turnLightOff() {
    HaloAuthResponse haloAuthResponse = getAuthToken();
    String jsonDisableLedLight = new StringBuilder().append("{").append("\"id\":\"2005010779M\",")
        .append("\"dimmer\":\"OFF\",").append("\"downlight\":\"false\"").
            append("}").toString();

    sendPutRequest(haloAuthResponse, jsonDisableLedLight);
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

  private HaloAuthResponse getAuthToken() {
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
}
