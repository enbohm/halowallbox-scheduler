package se.enbohms.halo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class TimeService {

  private static final Logger LOG = Logger.getLogger(TimeService.class);
  private static final Jsonb JSONB = JsonbBuilder.create();

  @Inject
  @RestClient
  HttpTimeClient httpTimeClient;

  private LocalTime sunsetTime;
  private LocalDate lastUpdated;

  public synchronized LocalTime getSunsetTime() {
    if (needRefetch()) {
      this.sunsetTime = fetchSunsetTime();
      this.lastUpdated = LocalDate.now();
      LOG.info("Fetched new sunset time " + sunsetTime);
    }
    return sunsetTime;
  }

  private boolean needRefetch() {
    return this.sunsetTime == null || LocalDate.now().isAfter(lastUpdated);
  }

  private LocalTime fetchSunsetTime() {
    Response response = httpTimeClient.fetchSunsetTime("56.170172", "14.863128", 0);
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
