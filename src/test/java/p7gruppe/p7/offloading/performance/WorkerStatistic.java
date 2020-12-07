package p7gruppe.p7.offloading.performance;

import p7gruppe.p7.offloading.performance.mock.MockUser;

public class WorkerStatistic extends Statistic{

    private long lastRegisteredTime = 0L;
    private long idleTime = 0;
    private long processingTime = 0;

    boolean isIdle = true;

    private final MockUser owner;
    public WorkerStatistic(MockUser owner) {
        this.owner = owner;
    }

    @Override
    public void startRecording(long startTime){
        super.startRecording(startTime);
        lastRegisteredTime = startTime;
    }

    @Override
    public void stopRecording(long stopTime){
        super.stopRecording(stopTime);
        updateStatusTime(stopTime);
    }

    protected void registerIdleStatus(long time, boolean updatedIsIdle) {
        assertRecording("registerIdleStatus()");
        updateStatusTime(time);
        isIdle = updatedIsIdle;
    }

    private void updateStatusTime(long newRegisterTime){
        long difference = newRegisterTime - lastRegisteredTime;
        if (isIdle) {
           idleTime += difference;
        } else {
            processingTime += difference;
        }
        lastRegisteredTime = newRegisterTime;
    }

    public long getIdleTime(){
        assertStopped("getIdleTime()");
        return idleTime;
    }

    public long getProcessingTime(){
        assertStopped("getProcessingTime()");
        return processingTime;
    }

    public MockUser getOwner(){
        return owner;
    }

    public boolean isMalicious(){
        return owner.isMalicious;
    }
}
