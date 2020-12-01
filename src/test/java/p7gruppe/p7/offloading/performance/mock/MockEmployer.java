package p7gruppe.p7.offloading.performance.mock;

import p7gruppe.p7.offloading.performance.APISupplier;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MockEmployer {

    private final MockUser mockUser;
    private final APISupplier apiSupplier;
    private final JobSpawner jobSpawner;

    private long lastUpdateTime = 0L;

    public MockEmployer(MockUser mockUser, APISupplier apiSupplier, JobSpawner jobSpawner) {
        this.mockUser = mockUser;
        this.apiSupplier = apiSupplier;
        this.jobSpawner = jobSpawner;
    }

    public void update(){

    }

    private void login(){
        // LoginController
        throw new NotImplementedException();
    }

    private void uploadJob(){
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
