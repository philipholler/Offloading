package p7gruppe.p7.offloading.performance.mock;

import kotlin.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import p7gruppe.p7.offloading.api.JobsApi;
import p7gruppe.p7.offloading.model.Job;
import p7gruppe.p7.offloading.performance.APISupplier;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MockEmployer implements Updatable {

    private enum JobStatus {
        PROCESSING, DONE_CORRECT, DONE_WRONG
    }

    public final MockUser mockUser;

    private final JobSpawner jobSpawner;
    private final APISupplier apiSupplier;

    private long lastUpdateTime = 0L;
    private JobsApi jobsApi;

    private long requestIntervalMillis = 5L * 1000L;
    private long nextRequestTime = 0L;

    private int jobsPosted = 0;

    public MockEmployer(MockUser mockUser, APISupplier apiSupplier, JobSpawner jobSpawner) {
        this.mockUser = mockUser;
        this.apiSupplier = apiSupplier;
        this.jobSpawner = jobSpawner;
        this.jobsApi = apiSupplier.jobsApi;
    }

    public void update(){
        Optional<MockJob> optionalJob = jobSpawner.pollJob();
        optionalJob.ifPresent(this::uploadJob);

        if (nextRequestTime < System.currentTimeMillis()) {
            getJobStatuses();
        }
    }

    private void uploadJob(MockJob mockJob){
        System.out.println("MockEmployer_uploadJob: Uploading job : " + jobsPosted + " from " + mockUser.userCredentials.getUsername());
        ResponseEntity<Void> responseEntity = apiSupplier.jobsApi.postJob(mockUser.userCredentials, mockJob.answersNeeded, String.valueOf(jobsPosted), Integer.MAX_VALUE, mockJob.getComputationTimeAsBase64Bytes());
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Could not upload job from mock employer : " + mockUser.userCredentials);
        }
        jobsPosted++;
    }

    private void getJobStatuses(){
        ResponseEntity<List<Job>> responseEntity = apiSupplier.jobsApi.getJobsForUser(mockUser.userCredentials);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Got error when retrieving job statuses from the server");
        }

        for (Job job : responseEntity.getBody()) {

        }

        throw new NotImplementedException();
    }

    private void downloadResult(int jobID){
        // JobController
        throw new NotImplementedException();
    }
}