package p7gruppe.p7.offloading;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import p7gruppe.p7.offloading.api.AssignmentsApiController;
import p7gruppe.p7.offloading.api.JobsApiController;
import p7gruppe.p7.offloading.api.UsersApiController;
import p7gruppe.p7.offloading.performance.APISupplier;
import p7gruppe.p7.offloading.performance.mock.MockUser;
import p7gruppe.p7.offloading.performance.mock.MockUserGenerator;
import p7gruppe.p7.offloading.scheduling.JobScheduler;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest()
class OffloadingApplicationTests {

	@Autowired
    UsersApiController usersApiController;
    @Autowired
    JobsApiController jobsApiController;
    @Autowired
    AssignmentsApiController assignmentsApiController;

	@Autowired
	JobScheduler scheduler;

	@Test
	void exampleTest() throws Exception {
	    long randomSeed = 123456789L;
	    double proportionMalicious = 0.1d;
        APISupplier apiSupplier = new APISupplier(usersApiController, assignmentsApiController, jobsApiController);
        MockUserGenerator userGenerator = new MockUserGenerator(randomSeed, proportionMalicious);
        List<MockUser> users = userGenerator.generateUsers(100, apiSupplier);

        int amountOfMaliciousUsers = 0;
        for (MockUser mockUser : users) if (mockUser.isMalicious) amountOfMaliciousUsers += 1;

        assertEquals(100, users.size());
        assertEquals(10, amountOfMaliciousUsers);
	}

}
