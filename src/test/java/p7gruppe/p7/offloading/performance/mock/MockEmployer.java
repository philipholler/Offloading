package p7gruppe.p7.offloading.performance.mock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import p7gruppe.p7.offloading.api.JobsApi;
import p7gruppe.p7.offloading.data.enitity.JobEntity.JobStatus;
import p7gruppe.p7.offloading.model.Job;
import p7gruppe.p7.offloading.model.JobFiles;
import p7gruppe.p7.offloading.model.Jobresult;
import p7gruppe.p7.offloading.performance.APISupplier;
import p7gruppe.p7.offloading.performance.JobStatistic;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static java.lang.System.currentTimeMillis;

public class MockEmployer implements Updatable {

    public final MockUser mockUser;

    private final JobSpawner jobSpawner;
    private final APISupplier apiSupplier;

    private long lastUpdateTime = 0L;
    private JobsApi jobsApi;

    private long requestIntervalMillis = 2L * 1000L;
    private long nextRequestTime = 0L;

    private int jobsPosted = 0;
    private final List<JobStatistic> postedJobs = new ArrayList<>();

    private HashMap<String, Boolean> hasDownloadedResult = new HashMap<>();

    public MockEmployer(MockUser mockUser, APISupplier apiSupplier, JobSpawner jobSpawner) {
        this.mockUser = mockUser;
        this.apiSupplier = apiSupplier;
        this.jobSpawner = jobSpawner;
        this.jobsApi = apiSupplier.jobsApi;
    }

    public void update(){
        Optional<MockJob> optionalJob = jobSpawner.pollJob();
        optionalJob.ifPresent(this::uploadJob);

        if (nextRequestTime < currentTimeMillis()) {
            getJobStatuses();
        }
    }

    private void uploadJob(MockJob mockJob){
        String jobName = String.valueOf(jobsPosted);
        JobStatistic jobStatistic = new JobStatistic(jobName, mockJob.computationTimeMillis);
        hasDownloadedResult.put(jobName, false);

        System.out.println("MockEmployer_uploadJob: Uploading job : " + jobsPosted + " from " + mockUser.userCredentials.getUsername());
        ResponseEntity<Void> responseEntity = apiSupplier.jobsApi.postJob(mockUser.userCredentials, mockJob.answersNeeded, String.valueOf(jobsPosted), Integer.MAX_VALUE, mockJob.getComputationTimeAsBase64Bytes());
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Could not upload job from mock employer : " + mockUser.userCredentials);
        }
        postedJobs.add(jobStatistic);
        jobsPosted++;
    }

    private void getJobStatuses(){
        ResponseEntity<List<Job>> responseEntity = apiSupplier.jobsApi.getJobsForUser(mockUser.userCredentials);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Got error when retrieving job statuses from the server");
        }

        for (Job job : responseEntity.getBody()) {
            JobStatistic jobStat = getJobStat(job.getName());
            JobStatus updatedJobStatus = JobStatus.valueOf(job.getStatus());
            jobStat.registerStatus(updatedJobStatus, System.currentTimeMillis());

            boolean finishedProcessing = (updatedJobStatus == JobStatus.DONE || updatedJobStatus == JobStatus.DONE_CONFLICTING_RESULTS);
            if (finishedProcessing && !hasDownloadedResult.get(job.getName())) {
                downloadResult(job.getId());
            }

        }

        throw new NotImplementedException();
    }

    private JobStatistic getJobStat(String name){
        Optional<JobStatistic> optJobStats = postedJobs.stream().findFirst();
        if (!optJobStats.isPresent())
            throw new RuntimeException("Job " + name + " missing from JobStatistics");

        return optJobStats.get();
    }

    private void downloadResult(long jobID){
        ResponseEntity<JobFiles> response = apiSupplier.jobsApi.getJobResult(jobID, mockUser.userCredentials);

        // todo : register correctness
        throw new NotImplementedException();
    }

    public List<JobStatistic> getPostedJobs() {
        return postedJobs;
    }
}