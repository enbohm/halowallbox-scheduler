package se.enbohms.halo.restclients;

import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/v4")
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
