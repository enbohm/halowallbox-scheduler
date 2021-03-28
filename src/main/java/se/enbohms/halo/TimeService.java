package se.enbohms.halo;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.logging.Logger;

@ApplicationScoped
public class TimeService {

  private static final Logger LOG = Logger.getLogger(TimeService.class);

  private static final String SUNRISE_API_URL =
      "https://api.sunrise-sunset.org/json?lat=56" + ".170172&lng=14.863128&formatted=0";
  private static final Jsonb JSONB = JsonbBuilder.create();

  private LocalTime sunsetTime;
  private LocalDate lastUpdated;

  public synchronized LocalTime getSunsetTime() {
    if (needRefresh()) {
      this.sunsetTime = fetchSunsetTime();
      this.lastUpdated = LocalDate.now();
      LOG.info("Fetched new sunset time " + sunsetTime);
    }
    return sunsetTime;
  }

  private boolean needRefresh() {
    return this.sunsetTime == null || LocalDate.now().isAfter(lastUpdated);
  }

  private LocalTime fetchSunsetTime() {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(URI.create(SUNRISE_API_URL));
    Response response = target.request(MediaType.APPLICATION_JSON).get();

    SunsetSunriseData data = JSONB
        .fromJson(response.readEntity(String.class).split(":", 2)[1], SunsetSunriseData.class);
    return ZonedDateTime.parse(data.sunset, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        .withZoneSameInstant(ZoneId.of("Europe/Stockholm")).toLocalTime();
  }

  public static class SunsetSunriseData {

    private String sunset;
    private String sunrise;

    public void setSunset(String sunset) {
      this.sunset = sunset;
    }

    public void setSunrise(String sunrise) {
      this.sunrise = sunrise;
    }
  }
}
