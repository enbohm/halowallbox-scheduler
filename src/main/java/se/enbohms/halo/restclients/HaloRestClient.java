package se.enbohms.halo.restclients;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/v3")
@RegisterRestClient(configKey = "halo-api")
public interface HaloRestClient {

  @POST
  @Path("/auth/login")
  Response getAuthToken(@HeaderParam("apiKey") String apiKey, HaloLoginPayload loginPayload);

  @PUT
  @Path("/chargepoints/{wallboxId}/settings")
  Response changeSettings(@HeaderParam("Authorization") String apiToken,
      @PathParam("wallboxId") String wallboxId, HaloSettingsPayload settingsPayload);
}
