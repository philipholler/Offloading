package p7gruppe.p7.offloading.performance;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MockWorker {

    public final double CPU_FACTOR;
    public final boolean MALICIOUS;
    public final String ID;

    public MockWorker(double cpu_factor, boolean malicious, String id) {
        CPU_FACTOR = cpu_factor;
        MALICIOUS = malicious;
        ID = id;
    }

    public void update() {
        throw new NotImplementedException();
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
