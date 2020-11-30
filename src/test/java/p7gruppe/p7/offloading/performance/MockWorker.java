package p7gruppe.p7.offloading.performance;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MockWorker {

    public final float CPU_FACTOR;
    public final boolean MALICIOUS;
    public final String ID;

    public MockWorker(float cpu_factor, boolean malicious, String id) {
        CPU_FACTOR = cpu_factor;
        MALICIOUS = malicious;
        ID = id;
    }

    public void update() {
        throw new NotImplementedException();
    }

    private void requestNewJob() {
        throw new NotImplementedException();
    }

    private void submitFakeResult() {

    }

}
