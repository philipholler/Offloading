package p7gruppe.p7.offloading.performance;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Tag("performance")
@SpringBootTest
public class PerformanceTest {

    static String pathToTestDataDir = System.getProperty("user.dir") + File.separator + "test_data" + File.separator;
    static String pathToStatisticsFolder = System.getProperty("user.dir") + File.separator + "statistics" + File.separator;

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

    @Autowired
    Environment environment;

    static final long RANDOM_SEED = 123456789L;
    private APISupplier apiSupplier;
    private RepositorySupplier repositorySupplier;

    @BeforeEach
    void setup() {
        apiSupplier = new APISupplier(usersApiController, assignmentsApiController, jobsApiController);
        repositorySupplier = new RepositorySupplier(assignmentRepository, jobRepository, userRepository, deviceRepository);
    }

    @BeforeAll
    static void removeOldStatisticsResults(){
        File resultDirFile = new File(pathToStatisticsFolder);
        for(File f : resultDirFile.listFiles()){
            try {
                if(f.isDirectory()){
                    FileUtils.deleteDirectory(f);
                } else {
                    f.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @BeforeEach
    void resetRepositories() {
        deviceRepository.deleteAll();
        jobRepository.deleteAll();
        assignmentRepository.deleteAll();
        userRepository.deleteAll();
        ServerStatistic.reset();
    }

    @AfterEach
    public void cleanup(){
        File resultDirFile = new File(pathToTestDataDir);
        try {
            FileUtils.deleteDirectory(resultDirFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void performanceTest_shortTermTest() {
        int userCount = 80, deviceCount = 300, employerCount = 80;
        UserBaseFactory userBaseFactory = new UserBaseFactory(apiSupplier);
        UserBase userBase = userBaseFactory.generateUserBaseSomeUsersWithoutWorkers(RANDOM_SEED, userCount, deviceCount, employerCount);
        userBase.initializeUserBase();

        long testDurationMillis = 60L * 1000L;
        long endTime = System.currentTimeMillis() + testDurationMillis;
        userBase.startSimulation();
        while (System.currentTimeMillis() < endTime) {
            userBase.update();
        }
        userBase.stopSimulation();

        StatisticsSummary summary = new StatisticsSummary(userBase, repositorySupplier);

        System.out.println("MAX compute time : " + summary.getMaximumTimeFromUploadTillProcessedMillis() / 1000);
        System.out.println("Average upload to processed time : " + summary.getAverageJobTimeForFinishedJobsMillis() / 1000);
        System.out.println("Results: Malicious/Total : " + summary.getAmountOfMaliciousResults() + " / " + summary.getAmountOfCompletedJobs());
        System.out.println("Average confidence : " + summary.averageConfidence());
        System.out.println("Total Throughput : " + summary.getTotalThroughput());
        System.out.println("Confidence over time: ");
        for (DataPoint<Double> dataPoint : summary.confidenceDataPoints()) {
            System.out.print("(" + dataPoint.timestamp / 1000 + ", " + dataPoint.value + "), ");
        }
        System.out.println();

        // System.out.println("Server view of user cpu contribution: " + Arrays.toString(userC));
        // System.out.println("Worker activation time: " + summary.getActivationOverTime("1"));
        String targetUser = "-1";
        for (MockWorker mockWorker : userBase.getWorkers()) {
            if (mockWorker.deviceId.getImei().equals("1")) {
                targetUser = mockWorker.owner.userCredentials.getUsername();
            }
        }

        List<DataPoint<Long>> serverCPUTime = ServerStatistic.getCPUTimeDataPoints(targetUser);
        System.out.println("User activation time: ");
        System.out.println(Arrays.toString(serverCPUTime.stream().map(((dp) -> dp.timestamp)).toArray()));
        System.out.println(Arrays.toString(serverCPUTime.stream().map(((dp) -> dp.value)).toArray()));

        System.out.println("User activation time: ");
        System.out.println(Arrays.toString(summary.getActivationOverTime("1").stream().map(((dp) -> dp.timestamp)).toArray()));
        System.out.println(Arrays.toString(summary.getActivationOverTime("1").stream().map(((dp) -> dp.value)).toArray()));


        List<StatPoint> statPoints = new ArrayList<>();

        String profile = environment.getActiveProfiles()[0];

        statPoints.add(new StatPoint("Amount of posted jobs", String.valueOf(summary.getAmountOfPostedJobs())));
        statPoints.add(new StatPoint("Amount of completed jobs", String.valueOf(summary.getAmountOfCompletedJobs())));
        statPoints.add(new StatPoint("Amount of incorrect results", String.valueOf(summary.getAmountOfMaliciousResults())));
        statPoints.add(new StatPoint("Correct 100% confidence results (Throughput)", String.valueOf(summary.getTotalThroughput())));

        statPoints.add(new StatPoint("Average confidence", String.valueOf(summary.averageConfidence())));
        statPoints.add(new StatPoint("Average job completion time", String.valueOf(summary.getAverageJobTimeForFinishedJobsMillis())));
        statPoints.add(new StatPoint("Maximum job completion time", String.valueOf(summary.getMaximumTimeFromUploadTillProcessedMillis())));

        ExcelWriter excelWriter = new ExcelWriter();
        excelWriter.writeStatPoints(profile + File.separator + "Overview.xlsx", statPoints);
        excelWriter.writeDataPoints(profile + File.separator + "Throughput.xlsx", summary.getThroughputOverTime(5000), "Millis since start", "100% Confidence jobs completed");
        excelWriter.writeDataPoints(profile + File.separator + "Confidence_over_time.xlsx", summary.confidenceDataPoints(), "Time", "Confidence");
        excelWriter.writeDataPoints(profile + File.separator + "Banked_time_Completion_time.xlsx", summary.getBankedTimeAndJobTime(), "Banked Time", "Job Completion Time");
        excelWriter.writeMultiDataPoints(profile + File.separator + "Activation_time_vs_banked_time.xlsx",
                Arrays.asList(serverCPUTime, summary.getActivationOverTime("1")), new String[]{"Time", "Banked Time", "Activation Time"});
    }

    @Test
    void performanceTest_jobOverload(){

    }


}
