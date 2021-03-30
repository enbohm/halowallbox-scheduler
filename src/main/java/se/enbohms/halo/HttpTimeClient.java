package se.enbohms.halo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/json")
@RegisterRestClient(configKey = "sunset-api")
public interface HttpTimeClient {

  @GET
  Response fetchSunsetTime(@QueryParam("lat") String lat, @QueryParam("lng") String lgn,
      @QueryParam("formatted") int formatted);
}
