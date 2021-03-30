package se.enbohms.halo;

import io.quarkus.scheduler.Scheduled;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class HaloScheduler {

  private final AtomicBoolean isLedOn = new AtomicBoolean(false);
  private static final LocalTime END_TIME = LocalTime.of(23, 00);

  @Inject
  HaloService haloService;

  @Inject
  TimeService timeService;

  @Scheduled(every = "60s")
  void scheduleHaloOnOff() {
    LocalTime now = LocalTime.now();
    if (now.isAfter(timeService.getSunsetTime()) && now.isBefore(END_TIME) && !isLedOn.get()) {
      this.haloService.turnLightOn();
      isLedOn.set(true);
    } else if (now.isAfter(LocalTime.of(23, 00)) && isLedOn.get()) {
      this.haloService.turnLightOff();
      isLedOn.set(false);
    }
  }
}

