package p7gruppe.p7.offloading.scheduling;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;

@EnabledIf(expression = "#{environment['spring.profiles.active'] == 'economic-performance-test'}", loadContext = true)
@SpringBootTest()
class EconomicJobSchedulerTest {
    @Test
    void testTest() {
        System.out.println("runs only on economic-test profile!");
    }
}