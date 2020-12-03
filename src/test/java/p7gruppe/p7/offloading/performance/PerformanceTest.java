package p7gruppe.p7.offloading.performance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
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

import java.util.List;

@Tag("performance")
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
    void performanceTest_defaultUserBase() {
        int userCount = 150, deviceCount = 165, employerCount = 80;
        UserBaseFactory userBaseFactory = new UserBaseFactory(apiSupplier);
        UserBase userBase = userBaseFactory.generateDefaultUserBase(RANDOM_SEED, userCount, deviceCount, employerCount);
        userBase.initializeUserBase();

        long testDurationMillis = 10L * 1000L;
        long endTime = System.currentTimeMillis() + testDurationMillis;
        while (System.currentTimeMillis() < endTime) {
            userBase.update();
        }

        List<JobStatistic> jobStatistics = userBase.getJobStatistics();
        for (JobStatistic job : jobStatistics) {
            System.out.println(job.isJobCompleted());
            if (job.isJobCompleted()) System.out.println("Correct result: " + job.isResultCorrect());
        }
    }

    @Test
    void performanceTest_jobOverload(){

    }


}
