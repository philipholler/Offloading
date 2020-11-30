package p7gruppe.p7.offloading.performance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import p7gruppe.p7.offloading.api.AssignmentsApiController;
import p7gruppe.p7.offloading.api.JobsApiController;
import p7gruppe.p7.offloading.api.UsersApiController;
import p7gruppe.p7.offloading.data.repository.UserRepository;

import javax.validation.constraints.AssertTrue;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest()
public class MockGenerationTest {

    @Autowired
    UsersApiController usersApiController;
    @Autowired
    JobsApiController jobsApiController;
    @Autowired
    AssignmentsApiController assignmentsApiController;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void resetRepositories(){
        userRepository.deleteAll();
    }

    @Test
    void generateUsers_proportionOfMaliciousUsers() throws Exception {
        long randomSeed = 123456789L;
        double proportionMalicious = 0.15d;
        APISupplier apiSupplier = new APISupplier(usersApiController, assignmentsApiController, jobsApiController);
        UserGenerator userGenerator = new UserGenerator(randomSeed, proportionMalicious);
        List<MockUser> users = userGenerator.generateUsers(100, apiSupplier);

        int amountOfMaliciousUsers = 0;
        for (MockUser mockUser : users) if (mockUser.isMalicious) amountOfMaliciousUsers += 1;

        assertEquals(100, users.size());
        assertEquals(15, amountOfMaliciousUsers);
    }

    @Test
    void registerUsers_usersExistsInUserRepository() throws Exception {
        long randomSeed = 123456789L;
        double proportionMalicious = 0.15d;
        APISupplier apiSupplier = new APISupplier(usersApiController, assignmentsApiController, jobsApiController);
        UserGenerator userGenerator = new UserGenerator(randomSeed, proportionMalicious);
        List<MockUser> users = userGenerator.generateUsers(100, apiSupplier);

        // Register users
        for (MockUser mockUser : users) mockUser.register();

        // Assert that they are now registered in the repository
        for (MockUser mockUser : users) assertTrue(userRepository.userExists(mockUser.userCredentials.getUsername()));
    }

}
