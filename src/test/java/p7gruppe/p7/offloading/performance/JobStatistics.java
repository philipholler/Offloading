package p7gruppe.p7.offloading.performance;

public class JobStatistics {

    public final String jobName;

    private long startTime = 0L;
    private long finishTime = -1L;
    private long statisticEndTime = -1L;
    private boolean hasFinished = false;

    private final int expectedCPUTime;
    private boolean resultCorrect = false;

    public JobStatistics(String jobName, int expectedCPUTime) {
        this.jobName = jobName;
        this.expectedCPUTime = expectedCPUTime;
    }

    public long getFinishTime() {
        if (!hasFinished)
            throw new RuntimeException("Called getFinishTime() on job that did not finish");
        return finishTime;
    }

    public long getProcessingTime() {
        if (hasFinished) return finishTime - startTime;
        return statisticEndTime - startTime;
    }

    public void registerAsFinished(long finishTime, boolean resultCorrect) {
        hasFinished = true;
        this.resultCorrect = resultCorrect;
        this.finishTime = finishTime;
        this.statisticEndTime = finishTime;
    }

    public void registerAsUnfinished(long time) {
        hasFinished = false;
        statisticEndTime = finishTime;
    }

    public boolean isResultCorrect(){
        if (!hasFinished)
            throw new RuntimeException("Called isResultCorrect() on job that does not have a result (job did not finish)");
        return resultCorrect;
    }

}
