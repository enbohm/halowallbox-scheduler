package se.enbohms.halo.restclients;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/json")
@RegisterRestClient(configKey = "sunset-api")
public interface SunsetTimeClient {

  @GET
  Response fetchSunsetTime(@QueryParam("lat") String lat, @QueryParam("lng") String lgn,
      @QueryParam("formatted") int formatted);
}
