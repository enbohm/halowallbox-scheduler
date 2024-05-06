package se.enbohms.halo.service;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import se.enbohms.halo.restclient.HaloLoginPayload;
import se.enbohms.halo.restclient.HaloRestClient;
import se.enbohms.halo.restclient.HaloSettingsPayload;

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
    var payload = new HaloSettingsPayload(wallboxId, "Medium", "false");
    sendPutRequest(getToken(), payload);
    LOG.info("TURNING ON LED");
  }

  public void turnLightOff() {
    var payload = new HaloSettingsPayload(wallboxId, "OFF", "false");
    sendPutRequest(getToken(), payload);
    LOG.info("TURNING OFF LED");
  }

  private void sendPutRequest(HaloTokenResponse tokenResponse, HaloSettingsPayload payload) {
    var putResponse = haloRestClient
        .changeSettings("Bearer " + tokenResponse.token(), wallboxId, payload);
    LOG.info("PUT status code " + putResponse.getStatus());
    LOG.info("PUT response body " + putResponse.readEntity(String.class));
  }

  private HaloTokenResponse getToken() {
    var response = haloRestClient.getAuthToken(apiKey, new HaloLoginPayload(userName, pwd));
    return response.readEntity(HaloTokenResponse.class);
  }
}
