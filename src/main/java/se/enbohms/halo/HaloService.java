package se.enbohms.halo;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import se.enbohms.halo.restclients.HaloLoginPayload;
import se.enbohms.halo.restclients.HaloRestClient;
import se.enbohms.halo.restclients.HaloSettingsPayload;

@ApplicationScoped
public class HaloService {

  private static final Logger LOG = Logger.getLogger(HaloService.class);

  @ConfigProperty(name = "halo.apikey")
  String apiKey;
  @ConfigProperty(name = "halo.username")
  String userName;
  @ConfigProperty(name = "halo.pwd")
  String pwd;
  @ConfigProperty(name = "halo.wallbox.id")
  String wallboxId;

  @Inject
  @RestClient
  HaloRestClient haloRestClient;

  @PostConstruct
  public void init() {
    LOG.info("Initializing Halo Service using API-key " + apiKey);
  }

  public void turnLightOn() {
    HaloAuthResponse haloAuthResponse = getAuthToken();
    HaloSettingsPayload payload = new HaloSettingsPayload(wallboxId, "Medium", "false");
    sendPutRequest(haloAuthResponse, payload);
    LOG.info("TURNING ON LED");
  }

  public void turnLightOff() {
    HaloAuthResponse haloAuthResponse = getAuthToken();
    HaloSettingsPayload payload = new HaloSettingsPayload(wallboxId, "OFF", "false");
    sendPutRequest(haloAuthResponse, payload);
    LOG.info("TURNING OFF LED");
  }

  private void sendPutRequest(HaloAuthResponse authResponse, HaloSettingsPayload payload) {
    Response putResponse = haloRestClient
        .changeSettings("Bearer " + authResponse.getToken(), wallboxId, payload);
    LOG.info("PUT status code " + putResponse.getStatus());
    LOG.info("PUT response body " + putResponse.readEntity(String.class));
  }

  private HaloAuthResponse getAuthToken() {
    Response response = haloRestClient.getAuthToken(apiKey, new HaloLoginPayload(userName, pwd));
    return response.readEntity(HaloAuthResponse.class);
  }
}
