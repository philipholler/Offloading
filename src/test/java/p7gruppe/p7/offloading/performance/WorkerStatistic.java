package p7gruppe.p7.offloading.performance;

import p7gruppe.p7.offloading.performance.mock.MockUser;
import p7gruppe.p7.offloading.statistics.DataPoint;

import java.util.ArrayList;
import java.util.List;

public class WorkerStatistic extends Statistic {

    private long lastStatusUpdateTime = 0L;
    boolean currentIsActiveStatus = false;
    private long idleTime = 0;
    private long processingTime = 0;

    private List<DataPoint<Long>> contributionOverTime = new ArrayList<>();

    private final MockUser owner;

    public WorkerStatistic(MockUser owner) {
        this.owner = owner;
    }

    @Override
    public void startRecording(long startTime) {
        super.startRecording(startTime);
        contributionOverTime.add(new DataPoint<>(startTime, 0L));
        lastStatusUpdateTime = startTime;
    }

    public List<DataPoint<Long>> getContributionOverTime() {
        return contributionOverTime;
    }

    @Override
    public void stopRecording(long stopTime) {
        super.stopRecording(stopTime);
        updateStatusTime(stopTime);
    }

    public void registerActiveStatus(long time, boolean newIsActive) {
        assertRecording("registerIdleStatus()");
        if (newIsActive == currentIsActiveStatus) return;
        updateStatusTime(time);
        currentIsActiveStatus = newIsActive;
    }

    private void updateStatusTime(long newRegisterTime) {
        long difference = newRegisterTime - lastStatusUpdateTime;

        if (currentIsActiveStatus) { processingTime += difference; }
        else { idleTime += difference; }

        contributionOverTime.add(new DataPoint<>(newRegisterTime, processingTime));
        lastStatusUpdateTime = newRegisterTime;
    }

    public long getIdleTime() {
        assertStopped("getIdleTime()");
        return idleTime;
    }

    public long getProcessingTime() {
        assertStopped("getProcessingTime()");
        return processingTime;
    }

    public MockUser getOwner() {
        return owner;
    }

    public boolean isMalicious() {
        return owner.isMalicious;
    }
}
