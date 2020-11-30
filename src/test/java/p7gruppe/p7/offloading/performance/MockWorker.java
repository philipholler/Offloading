package p7gruppe.p7.offloading.performance;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MockWorker {

    public final double CPU_FACTOR;
    public final String ID;
    public final MockUser owner;
    private final APISupplier apiSupplier;

    public MockWorker(double cpu_factor, String deviceID, MockUser mockUser, APISupplier apiSupplier) {
        CPU_FACTOR = cpu_factor;
        this.owner = mockUser;
        ID = deviceID;
        this.apiSupplier = apiSupplier;
    }

    public void update() {
        throw new NotImplementedException();
    }

    public boolean isMalicious(){
        return owner.isMalicious;
    }

    private void login() {
        // login controller
        throw new NotImplementedException();
    }

    private void requestNewJob() {
        // assignmentController
        throw new NotImplementedException();
    }

    private void submitResult() {
        // assignmentController
        throw new NotImplementedException();
    }

    private void quitJob() {
        // assignmentController
        throw new NotImplementedException();
    }

}
