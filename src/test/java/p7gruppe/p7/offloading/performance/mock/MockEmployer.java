package p7gruppe.p7.offloading.performance.mock;

import p7gruppe.p7.offloading.performance.APISupplier;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Optional;

public class MockEmployer {

    public final MockUser mockUser;

    private final JobSpawner jobSpawner;
    private final APISupplier apiSupplier;

    private long lastUpdateTime = 0L;

    public MockEmployer(MockUser mockUser, APISupplier apiSupplier, JobSpawner jobSpawner) {
        this.mockUser = mockUser;
        this.apiSupplier = apiSupplier;
        this.jobSpawner = jobSpawner;
    }

    public void update(){
        Optional<MockJob> optionalJob = jobSpawner.pollJob();
        optionalJob.ifPresent(this::uploadJob);
    }

    private void login(){
        // LoginController
        throw new NotImplementedException();
    }

    private void uploadJob(MockJob mockJob){
        // JobController
        throw new NotImplementedException();
    }

    private void getJobStatuses(){
        // JobController
        throw new NotImplementedException();
    }

    private void downloadResult(int jobID){
        // JobController
        throw new NotImplementedException();
    }

}
