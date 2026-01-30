package se.enbohms.halo;

import io.quarkus.scheduler.Scheduled;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicBoolean;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import se.enbohms.halo.service.HaloService;
import se.enbohms.halo.service.TimeService;

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
    var now = timeService.getCurrentTime();
    var endTime = LocalTime.of(endHour, endMinute);
    var sunset = timeService.getSunsetTime();

    boolean shouldBeOn = isBetween(now, sunset, endTime);

    if (shouldBeOn && !isLedOn.get()) {
      this.haloService.turnLightOn();
      isLedOn.set(true);
    } else if (!shouldBeOn && isLedOn.get()) {
      this.haloService.turnLightOff();
      isLedOn.set(false);
    }
  }

  /**
   * Returns true if {@code time} is in the interval [start, end) (midnight crossing is not into account).
   */
  private static boolean isBetween(LocalTime time, LocalTime start, LocalTime end) {
    // Returns true for [start, end) when start < end.
    return !time.isBefore(start) && time.isBefore(end);
  }
}

