package se.enbohms.halo;

import static org.wildfly.common.Assert.assertNotNull;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import java.time.LocalTime;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(WiremockTimeServiceEndpoint.class)
public class TimeServiceTest {

  @Inject
  TimeService timeService;

  @Test
  void should_return_a_local_sunset_time() {
    LocalTime time = timeService.getSunsetTime();
    assertNotNull(time);
  }
}
