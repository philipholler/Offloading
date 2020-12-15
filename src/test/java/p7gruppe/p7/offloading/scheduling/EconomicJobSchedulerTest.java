package p7gruppe.p7.offloading.scheduling;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import p7gruppe.p7.offloading.api.AssignmentsApiController;
import p7gruppe.p7.offloading.api.JobsApiController;
import p7gruppe.p7.offloading.api.UsersApiController;
import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.data.repository.AssignmentRepository;
import p7gruppe.p7.offloading.data.repository.DeviceRepository;
import p7gruppe.p7.offloading.data.repository.JobRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;
import p7gruppe.p7.offloading.model.DeviceId;
import p7gruppe.p7.offloading.model.JobFiles;
import p7gruppe.p7.offloading.model.UserCredentials;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("correctness")
@EnabledIf(expression = "#{environment['spring.profiles.active'] == 'economic-performance-test'}", loadContext = true)
@SpringBootTest()
class EconomicJobSchedulerTest {

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
    JobScheduler scheduler;

    @Autowired
    Environment environment;

    @Test
    void economicSchedulerTest01() {
        // Create users
        UserEntity badUser = new UserEntity("user1", "secret");
        badUser.setCpuTimeContributedInMillis(0);
        badUser.setCpuTimeSpentInMillis(0);
        UserEntity goodUser = new UserEntity("user2", "secret");
        goodUser.setCpuTimeSpentInMillis(0);
        goodUser.setCpuTimeContributedInMillis(100);
        UserEntity workerUser = new UserEntity("user3", "secret");
        userRepository.saveAll(Arrays.asList(workerUser, badUser, goodUser));

        // Create jobs in system
        JobEntity goodUserJob = new JobEntity(goodUser, "somepath", "goodJob", 3,60);
        JobEntity badUserJob = new JobEntity(badUser, "someOtherPath", "badJob", 3, 60);
        jobRepository.saveAll(Arrays.asList(goodUserJob, badUserJob));

        // Create worker asking for job
        DeviceEntity pollingWorker = new DeviceEntity(workerUser, "007");
        deviceRepository.save(pollingWorker);

        // Get job from scheduler
        Optional<JobEntity> jobOpt = scheduler.assignJob(pollingWorker);

        // Assert that a job is present, and that the employer is the goodUser with most banked CPU time
        assertTrue(jobOpt.isPresent());
        JobEntity job = jobOpt.get();
        assertEquals(goodUser.getUserId(), job.employer.getUserId());
    }
}