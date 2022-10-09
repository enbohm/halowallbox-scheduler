package se.enbohms.halo;

import io.quarkus.scheduler.Scheduled;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class HaloScheduler {

  private final AtomicBoolean isLedOn = new AtomicBoolean(false);

  @ConfigProperty(name = "halo.end.hour")
  int endHour;

  @ConfigProperty(name = "halo.end.minute")
  int endMinute;

  @Inject
  HaloService haloService;

  @Inject
  TimeService timeService;

  @Scheduled(every = "60s")
  void scheduleHaloOnOff() {
    var now = LocalTime.now();
    var endTime = LocalTime.of(endHour, endMinute);
    if (now.isAfter(timeService.getSunsetTime()) && now.isBefore(endTime) && !isLedOn.get()) {
      this.haloService.turnLightOn();
      isLedOn.set(true);
    } else if (now.isAfter(endTime) && isLedOn.get()) {
      this.haloService.turnLightOff();
      isLedOn.set(false);
    }
  }
}

