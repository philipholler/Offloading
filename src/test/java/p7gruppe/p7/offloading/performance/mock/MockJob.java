package p7gruppe.p7.offloading.performance.mock;

import p7gruppe.p7.offloading.data.local.JobFileManager;
import p7gruppe.p7.offloading.util.ByteUtils;

public class MockJob {

    public final long computationTimeMillis;
    public final long requestedWorkers;

    private int jobID = -1;

    public MockJob(long computationTimeMillis, long requestedWorkers) {
        this.computationTimeMillis = computationTimeMillis;
        this.requestedWorkers = requestedWorkers;
    }

    /** Mock job data bytes represent the simulated cpu time of the job **/
    // Converts the computation time to bytes
    public byte[] getComputationTimeAsBase64Bytes(){
        return JobFileManager.encodeJobBytes(ByteUtils.longToBytes(computationTimeMillis));
    }

    public static long base64BytesToComputationTime(byte[] encoded){
        return ByteUtils.bytesToLong(JobFileManager.decodeJobByte64(encoded));
    }

    public int getJobID() {
        if (jobID == -1)
            throw new IllegalStateException("Tried to get job id, when it has not yet been assigned");
        return jobID;
    }
}
