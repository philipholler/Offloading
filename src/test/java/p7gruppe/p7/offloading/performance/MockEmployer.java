package p7gruppe.p7.offloading.performance;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MockEmployer {

    private final MockUser mockUser;
    private final APISupplier apiSupplier;

    public MockEmployer(MockUser mockUser, APISupplier apiSupplier) {
        this.mockUser = mockUser;
        this.apiSupplier = apiSupplier;
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
