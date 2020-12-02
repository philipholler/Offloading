package p7gruppe.p7.offloading.performance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import p7gruppe.p7.offloading.api.AssignmentsApiController;
import p7gruppe.p7.offloading.api.JobsApiController;
import p7gruppe.p7.offloading.api.UsersApiController;
import p7gruppe.p7.offloading.data.repository.AssignmentRepository;
import p7gruppe.p7.offloading.data.repository.DeviceRepository;
import p7gruppe.p7.offloading.data.repository.JobRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;
import p7gruppe.p7.offloading.performance.mock.*;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class PerformanceTest {

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
    @Autowired
    JobRepository jobRepository;
    @Autowired
    AssignmentRepository assignmentRepository;

    static final long RANDOM_SEED = 123456789L;
    private APISupplier apiSupplier;

    @BeforeEach
    void setup() {
        apiSupplier = new APISupplier(usersApiController, assignmentsApiController, jobsApiController);
    }

    @BeforeEach
    void resetRepositories() {
        deviceRepository.deleteAll();
        jobRepository.deleteAll();
        assignmentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void scheduler_performance1() {
        long randomSeed = 123456L;
        MockUserGenerator userGenerator = new MockUserGenerator(apiSupplier);
        List<MockUser> users = userGenerator.generateUsers(100, randomSeed);

        MockWorkerGenerator workerGenerator = new MockWorkerGenerator(apiSupplier);
        List<MockWorker> workers = workerGenerator.generateWorkers(150, users, randomSeed);

        MockEmployerGenerator employerGenerator = new MockEmployerGenerator(apiSupplier);
        List<MockEmployer> employers = employerGenerator.generateEmployers(50, users, randomSeed);

        for (MockUser user : users) user.register();
        for (MockWorker worker : workers) worker.login();

        List<Updatable> systemEntities = new ArrayList<>();
        systemEntities.addAll(workers);
        systemEntities.addAll(employers);


    }


}
