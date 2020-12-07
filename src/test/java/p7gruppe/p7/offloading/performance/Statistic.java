package p7gruppe.p7.offloading.performance;

public abstract class Statistic {

    private long startTime = 0L;
    private long endTime = 0L;

    boolean hasStarted = false;
    boolean hasStopped = false;

    public void startRecording(long startTime){
        if (hasStarted) throw new IllegalStateException("Attempted to start WorkerStatistic twice");
        this.startTime = startTime;
        hasStarted = true;
    }

    public void stopRecording(long stopTime){
        assertRecording("stopRecording(..)");
        hasStopped = true;
    }

    protected void assertRecording(String methodName){
        if (!hasStarted || hasStopped)
            throw new IllegalStateException("Cannot call " + methodName + " when recording is not progress");
    }

    protected void assertStopped(String methodName){
        if (!hasStopped)
            throw new IllegalStateException("Cannot call " + methodName + " when recording is not stopped");
    }



}
