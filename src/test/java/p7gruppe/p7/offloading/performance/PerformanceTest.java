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
import p7gruppe.p7.offloading.statistics.DataPoint;
import p7gruppe.p7.offloading.statistics.ServerStatistic;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
    private RepositorySupplier repositorySupplier;

    @BeforeEach
    void setup() {
        apiSupplier = new APISupplier(usersApiController, assignmentsApiController, jobsApiController);
        repositorySupplier = new RepositorySupplier(assignmentRepository, jobRepository, userRepository, deviceRepository);
    }

    @BeforeEach
    void resetRepositories() {
        deviceRepository.deleteAll();
        jobRepository.deleteAll();
        assignmentRepository.deleteAll();
        userRepository.deleteAll();
        ServerStatistic.reset();
    }

    @Test
    void performanceTest_shortTermTest() {
        int userCount = 80, deviceCount = 80, employerCount = 80;
        UserBaseFactory userBaseFactory = new UserBaseFactory(apiSupplier);
        UserBase userBase = userBaseFactory.generateDefaultUserBase(RANDOM_SEED, userCount, deviceCount, employerCount);
        userBase.initializeUserBase();

        long testDurationMillis = 50L * 1000L;
        long endTime = System.currentTimeMillis() + testDurationMillis;
        userBase.startSimulation();
        while (System.currentTimeMillis() < endTime) {
            userBase.update();
        }
        userBase.stopSimulation();

        StatisticsSummary summary = new StatisticsSummary(userBase, repositorySupplier);

        System.out.println("MAX compute time : " + summary.getMaximumTimeFromUploadTillProcessedMillis() / 1000);
        System.out.println("Average upload to processed time : " + summary.getAverageJobTimeForFinishedJobsMillis() / 1000);
        System.out.println("Results: Malicious/Total : " + summary.getAmountOfMaliciousResults() + " / " + summary.getAmountOfResults());
        System.out.println("Average confidence : " + summary.averageConfidence());
        System.out.println("Total Throughput : " + summary.getTotalThroughput());
        System.out.println(Arrays.toString(summary.getThroughputOverTime(1000)));
        System.out.println("Confidence over time: ");
        for (DataPoint<Double> dataPoint : summary.confidenceDataPoints()) {
            System.out.print("(" + dataPoint.timestamp / 1000 + ", " + dataPoint.value + "), ");
        }
        System.out.println();

        //System.out.println("Server view of user cpu contribution: " + Arrays.toString(userC));
        //System.out.println("Worker activation time: " + summary.getActivationOverTime("1"));
        String targetUser = "-1";
        for (MockWorker mockWorker : userBase.getWorkers()) {
            if (mockWorker.deviceId.getImei().equals("1")) {
                targetUser = mockWorker.owner.userCredentials.getUsername();
            }
        }

        List<DataPoint<Long>> userCPUTime = ServerStatistic.getCPUTimeDataPoints(targetUser);
        System.out.println("User activation time: ");
        System.out.println(Arrays.toString(userCPUTime.stream().map(((dp) -> dp.timestamp)).toArray()));
        System.out.println(Arrays.toString(userCPUTime.stream().map(((dp) -> dp.value)).toArray()));

        System.out.println("User activation time: ");
        System.out.println(Arrays.toString(summary.getActivationOverTime("1").stream().map(((dp) -> dp.timestamp)).toArray()));
        System.out.println(Arrays.toString(summary.getActivationOverTime("1").stream().map(((dp) -> dp.value)).toArray()));

        ExcelWriter excelWriter = new ExcelWriter();
        excelWriter.writeDataPoints("test" + File.separator + "singleDataSet.xlsx", userCPUTime, "x", "y");

        excelWriter.writeMultiDataPoints("test" + File.separator + "multiDataSet.xlsx", Arrays.asList(userCPUTime, summary.getActivationOverTime("1")), new String[]{"", "", ""});




    }

    @Test
    void performanceTest_jobOverload(){

    }


}
