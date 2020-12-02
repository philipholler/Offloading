package p7gruppe.p7.offloading.performance.mock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import p7gruppe.p7.offloading.data.local.JobFileManager;
import p7gruppe.p7.offloading.model.DeviceId;
import p7gruppe.p7.offloading.model.JobFiles;
import p7gruppe.p7.offloading.model.Jobresult;
import p7gruppe.p7.offloading.performance.APISupplier;
import p7gruppe.p7.offloading.util.ByteUtils;

import java.util.function.Function;

public class MockWorker implements Updatable {

    public static final int MALICIOUS_RESULT = 0;
    public static final int CORRECT_RESULT = 1;

    public final double CPU_FACTOR;
    public final DeviceId deviceId;
    public final MockUser owner;
    private final APISupplier apiSupplier;

    private boolean isWorkingJob = false;
    private JobFiles currentJob;
    private long currentJobFinishTime;
    private boolean isLoggedIn = false;

    private long getRequestIntervalMillis = 5000L;
    private long lastGetRequestTimeMillis = 0L;

    // Default activation policy is that it is always active
    private Function<Long, Boolean> activationPolicy = (time) -> true;

    public void setActivationPolicy(Function<Long, Boolean> activationPolicy) {
        this.activationPolicy = activationPolicy;
    }

    public MockWorker(double cpu_factor, String deviceID, MockUser mockUser, APISupplier apiSupplier) {
        CPU_FACTOR = cpu_factor;
        this.owner = mockUser;
        this.deviceId = new DeviceId().imei(deviceID);
        this.apiSupplier = apiSupplier;
    }

    public void update() {
        if (!isLoggedIn)
            throw new IllegalStateException("Device must be logged in before updating");

        long now = System.currentTimeMillis();
        boolean isActive = activationPolicy.apply(now);

        if (!isActive) {
            if (isWorkingJob) {
                quitJob();
            }
            return;
        }

        if (isWorkingJob) {
            if (System.currentTimeMillis() > currentJobFinishTime) {
                this.submitResult();
            }
        } else {
            if (System.currentTimeMillis() - lastGetRequestTimeMillis > getRequestIntervalMillis) {
                requestNewJob();
                lastGetRequestTimeMillis = System.currentTimeMillis();
            }
        }
    }

    public boolean isMalicious() {
        return owner.isMalicious;
    }

    public void login() {
        apiSupplier.usersApi.login(owner.userCredentials, deviceId);
        isLoggedIn = true;
    }

    private void requestNewJob() {
        ResponseEntity<JobFiles> jobResponse = apiSupplier.assignmentsApi.getJobForDevice(owner.userCredentials, deviceId);
        if (jobResponse.getStatusCode() == HttpStatus.OK) {
            currentJob = jobResponse.getBody();
            isWorkingJob = true;
            int jobCPUTime = ByteUtils.bytesToInt(JobFileManager.decodeFromBase64(currentJob.getData()));
            jobCPUTime *= CPU_FACTOR;
            currentJobFinishTime = System.currentTimeMillis() + jobCPUTime;
        }
    }

    private void submitResult() {
        int result;
        if (isMalicious()) {
            result = MALICIOUS_RESULT;
        } else {
            result = CORRECT_RESULT;
        }

        byte[] resultBytes = JobFileManager.encodeJobBytes(ByteUtils.intToBytes(result));
        Jobresult jobresult = new Jobresult().result(new JobFiles().data(resultBytes));
        apiSupplier.assignmentsApi.uploadJobResult(owner.userCredentials, deviceId, currentJob.getJobid(), jobresult);
        isWorkingJob = false;
        currentJob = null;
    }

    private void quitJob() {
        ResponseEntity<Void> response = apiSupplier.assignmentsApi.quitAssignment(owner.userCredentials, deviceId, currentJob.getJobid());
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Attempted job quit but not accepted by server : " + deviceId.toString());
        }
        isWorkingJob = false;
        currentJob = null;
    }

}
