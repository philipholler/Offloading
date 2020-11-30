package p7gruppe.p7.offloading.scheduling;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.EnabledIf;

@EnabledIf(expression = "#{environment['spring.profiles.active'] == 'fifo-test'}", loadContext = true)
@SpringBootTest()
class FIFOJobSchedulerTest {
    @Test
    void testTest() {
        System.out.println("runs only on fifo-test profile!");
    }
}