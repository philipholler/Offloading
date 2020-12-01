package p7gruppe.p7.offloading.performance.mock;

import p7gruppe.p7.offloading.data.local.JobFileManager;
import p7gruppe.p7.offloading.util.ByteUtils;

public class MockJob {

    public final int computationTimeMillis;
    public final int requestedWorkers;

    private int jobID = -1;

    public MockJob(int computationTimeMillis, int requestedWorkers) {
        if (computationTimeMillis < 0)
            throw new IllegalArgumentException("Job must have positive calculation time");
        if (requestedWorkers <= 0)
            throw new IllegalArgumentException("Job must have at least 1 requested worker");

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
