package se.enbohms.halo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        LocalTime result = timeService.getSunsetTime();
        assertNotNull(result, () -> "Time should not be null");
    }
}
