package se.enbohms.halo;

import io.quarkus.scheduler.Scheduled;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class HaloScheduler {

  private static final Logger LOG = Logger.getLogger(HaloScheduler.class);

  private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(Version.HTTP_2)
      .connectTimeout(Duration.ofSeconds(10)).build();

  private final AtomicBoolean isLedOn = new AtomicBoolean(false);

  @Inject
  HaloService haloService;

  @ConfigProperty(name = "halo.start.hour")
  int startHour;
  @ConfigProperty(name = "halo.start.minute")
  int startMinute;

  @Scheduled(every = "60s")
  void scheduleHaloOnOff() {
    LocalTime now = LocalTime.now();
    if (now.isAfter(LocalTime.of(startHour, startMinute)) && now.isBefore(LocalTime.of(23, 00))
        && !isLedOn.get()) {
      this.haloService.turnLightOn();
      isLedOn.set(true);
    } else if (now.isAfter(LocalTime.of(23, 00)) && isLedOn.get()) {
      this.haloService.turnLightOff();
      isLedOn.set(false);
    }
  }
}

