package p7gruppe.p7.offloading.repositorytest;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import p7gruppe.p7.offloading.api.AssignmentsApiController;
import p7gruppe.p7.offloading.api.JobsApiController;
import p7gruppe.p7.offloading.api.UsersApiController;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.data.repository.AssignmentRepository;
import p7gruppe.p7.offloading.data.repository.DeviceRepository;
import p7gruppe.p7.offloading.data.repository.JobRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;
import p7gruppe.p7.offloading.scheduling.JobScheduler;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("repositoryTest")
@EnabledIf(expression = "#{environment['spring.profiles.active'] == 'economic-performance-test'}",
        loadContext = true)
@SpringBootTest()
public class JobRepositoryTests {

    @Autowired
    UserRepository userRepository;
    @Autowired
    JobRepository jobRepository;

    @Test
    void jobRepositoryTest01(){
        // Test for getJobWithHighestUserPriorityFromOtherUser query
        // We want to test, that we indeed do not get our own job, even though we have the most banked time.
        // And that we in fact get a job from another user with the highest banked time

        // Create users
        UserEntity thisUser = new UserEntity("this", "secret");
        thisUser.setCpuTimeContributedInMillis(1000); thisUser.setCpuTimeSpentInMillis(0);
        UserEntity otherUserGood = new UserEntity("otherGood", "secret");
        otherUserGood.setCpuTimeContributedInMillis(500); otherUserGood.setCpuTimeSpentInMillis(0);
        UserEntity otherUserBad = new UserEntity("otherBad", "secret");
        otherUserBad.setCpuTimeContributedInMillis(0); otherUserGood.setCpuTimeSpentInMillis(100);
        userRepository.saveAll(Arrays.asList(thisUser, otherUserBad, otherUserGood));

        // Create jobs
        JobEntity thisUserJob = new JobEntity(thisUser, "thisPath", "thisJob", 3, 60);
        JobEntity otherUserGoodJob = new JobEntity(otherUserGood, "otherGoodPath", "otherGoodJob", 3, 60);
        JobEntity otherUserBadJob = new JobEntity(otherUserBad, "otherBadPath", "otherBadJob",3, 60);
        jobRepository.saveAll(Arrays.asList(thisUserJob, otherUserGoodJob, otherUserBadJob));

        // Use methods, that's being tested
        Optional<JobEntity> jobOpt = jobRepository.getJobWithHighestUserPriorityFromOtherUser(thisUser.getUserId());

        // Assert that the answer is correct
        assertTrue(jobOpt.isPresent());

        JobEntity job = jobOpt.get();
        assertEquals(otherUserGoodJob.getJobId(), job.getJobId());
    }
}
