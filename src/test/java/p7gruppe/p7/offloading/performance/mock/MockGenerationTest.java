package p7gruppe.p7.offloading.performance.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import p7gruppe.p7.offloading.api.AssignmentsApiController;
import p7gruppe.p7.offloading.api.JobsApiController;
import p7gruppe.p7.offloading.api.UsersApiController;
import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.repository.DeviceRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;
import p7gruppe.p7.offloading.performance.APISupplier;
import p7gruppe.p7.offloading.performance.RepositorySupplier;
import p7gruppe.p7.offloading.testutils.IteratorUtils;

import java.util.HashMap;
import java.util.HashSet;
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
    @Autowired
    DeviceRepository deviceRepository;

    static final long RANDOM_SEED = 123456789L;
    private APISupplier apiSupplier;

    @BeforeEach
    void setup() {
        apiSupplier = new APISupplier(usersApiController, assignmentsApiController, jobsApiController);
    }

    @BeforeEach
    void resetRepositories() {
        deviceRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void generateUsers_proportionOfMaliciousUsers() throws Exception {
        double proportionMalicious = 0.15d;
        MockUserGenerator userGenerator = new MockUserGenerator(apiSupplier);
        userGenerator.setProportionOfMaliciousUsers(proportionMalicious);
        List<MockUser> users = userGenerator.generateUsers(100, RANDOM_SEED);

        int amountOfMaliciousUsers = 0;
        for (MockUser mockUser : users) if (mockUser.isMalicious) amountOfMaliciousUsers += 1;

        assertEquals(100, users.size());
        assertEquals(15, amountOfMaliciousUsers);
    }

    @Test
    void registerUsers_usersExistsInUserRepository() throws Exception {
        double proportionMalicious = 0.15d;
        MockUserGenerator userGenerator = new MockUserGenerator(apiSupplier);
        userGenerator.setProportionOfMaliciousUsers(proportionMalicious);
        List<MockUser> users = userGenerator.generateUsers(100, RANDOM_SEED);

        // Register users
        for (MockUser mockUser : users) mockUser.register();

        // Assert that they are now registered in the repository
        for (MockUser mockUser : users) assertTrue(userRepository.userExists(mockUser.userCredentials.getUsername()));
    }

    @Test
    void generateWorkers_allUsersHave2Devices() {
        MockWorkerGenerator workerGenerator = new MockWorkerGenerator(apiSupplier);
        MockUserGenerator userGenerator = new MockUserGenerator(apiSupplier);

        List<MockUser> users = userGenerator.generateUsers(100, RANDOM_SEED);
        List<MockWorker> workers = workerGenerator.generateWorkers(200, users, RANDOM_SEED);

        HashMap<String, Integer> userToDeviceCount = new HashMap<String, Integer>();
        for (MockWorker worker : workers) {
            String username = worker.owner.userCredentials.getUsername();
            if (userToDeviceCount.containsKey(username)) {
                userToDeviceCount.put(username, userToDeviceCount.get(username) + 1);
            } else {
                userToDeviceCount.put(username, 1);
            }
        }

        assertEquals(200, workers.size());
        assertEquals(100, userToDeviceCount.keySet().size());

        for (MockWorker worker : workers) {
            assertEquals(2, userToDeviceCount.get(worker.owner.userCredentials.getUsername()));
        }
    }

    @Test
    void workerLogin_devicesRegisteredInServer() throws InterruptedException {
        MockWorkerGenerator workerGenerator = new MockWorkerGenerator(apiSupplier);
        MockUserGenerator userGenerator = new MockUserGenerator(apiSupplier);

        List<MockUser> users = userGenerator.generateUsers(100, RANDOM_SEED);
        List<MockWorker> workers = workerGenerator.generateWorkers(200, users, RANDOM_SEED);

        for (MockUser user : users) {
            user.register();
        }

        HashSet<String> mockWorkerIDs = new HashSet<>();
        for (MockWorker worker : workers) {
            worker.login();
            mockWorkerIDs.add(worker.deviceId.getImei());
        }

        HashSet<String> serverWorkerIDs = new HashSet<>();
        for (DeviceEntity device : deviceRepository.findAll()) {
            serverWorkerIDs.add(device.getImei());
        }

        assertEquals(mockWorkerIDs, serverWorkerIDs);
    }

    @Test
    void generateEmployers_allUsersAreEmployers() {
        MockUserGenerator userGenerator = new MockUserGenerator(apiSupplier);
        MockEmployerGenerator employerGenerator = new MockEmployerGenerator(apiSupplier);

        List<MockUser> users = userGenerator.generateUsers(100, RANDOM_SEED);
        List<MockEmployer> employers = employerGenerator.generateEmployers(users.size(), users, RANDOM_SEED);

        HashSet<String> employerUsers = new HashSet<>();
        for (MockEmployer employer : employers) {
            employerUsers.add(employer.mockUser.userCredentials.getUsername());
        }

        // We expect exactly the same amount of employers as users
        assertEquals(users.size(), employers.size());
        // We expected exactly the same amount of UNIQUE employers as users (ie. 1 employer per user)
        assertEquals(users.size(), employerUsers.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 63, 66, 126, 129, 254, 257, 159156156})
    void mockJob_cpuTimeByteConversion(int computationTime) {
        MockJob mockJob = new MockJob(computationTime, 2);

        byte[] encodedCpuTimeBytes = mockJob.getComputationTimeAsBase64Bytes();
        long decodedComputationTime = MockJob.base64BytesToComputationTime(encodedCpuTimeBytes);
        assertEquals(computationTime, decodedComputationTime);
    }

    @Test
    void generateUserBase_allRegistered() {
        int userCount = 100, workerCount = 150, employerCount = 70;
        UserBaseFactory userBaseFactory = new UserBaseFactory(apiSupplier, null);
        UserBase userBase = userBaseFactory.generateDefaultUserBase(RANDOM_SEED, userCount, workerCount, employerCount);
        userBase.initializeUserBase();

        int registeredUsers = IteratorUtils.countLength(userRepository.findAll());
        int registeredDevices = IteratorUtils.countLength(deviceRepository.findAll());

        assertEquals(userCount, registeredUsers);
        assertEquals(workerCount, registeredDevices);
    }


}
