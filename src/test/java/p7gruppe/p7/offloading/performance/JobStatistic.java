package p7gruppe.p7.offloading.performance;

import p7gruppe.p7.offloading.data.enitity.JobEntity;

public class JobStatistic {

    public final String jobName;

    private long uploadTime = 0L;
    private long finishTime = -1L;
    private long statisticEndTime = -1L;
    private boolean hasFinished = false;

    private final int expectedCPUTime;
    private boolean resultCorrect = true;
    private JobEntity.JobStatus status = null;

    private boolean assignedToAnyUser = false;
    private long assignmentTime = 0L;

    public JobStatistic(String jobName, int expectedCPUTime) {
        this.jobName = jobName;
        this.expectedCPUTime = expectedCPUTime;
    }

    public void registerStatus(JobEntity.JobStatus newStatus, long timeStampMillis){
        if (status == null || status.equals(newStatus)) return; // no update necessary

        switch (newStatus) {
            case WAITING:
                registerUpload(timeStampMillis);
                break;
            case PROCESSING:
                registerAssignment(timeStampMillis);
                break;
            case DONE:
                registerAsFinished(timeStampMillis);
                break;
            case DONE_CONFLICTING_RESULTS:
                break;
            default:
                throw new RuntimeException("No logic for handling job status update: "  + newStatus.name());
        }
    }

    public void registerUpload(long uploadTime){
        this.uploadTime = uploadTime;
        status = JobEntity.JobStatus.WAITING;
    }

    public long getFinishTime() {
        if (!hasFinished)
            throw new RuntimeException("Called getFinishTime() on job that did not finish");
        return finishTime;
    }

    public long getProcessingTime() {
        if (hasFinished) return finishTime - uploadTime;
        return statisticEndTime - uploadTime;
    }

    public void registerResultCorrectness(boolean isCorrect){
        this.resultCorrect = isCorrect;
    }

    public void registerAsFinished(long finishTime) {
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

    private void registerAssignment(long timeStampMillis){
        assignedToAnyUser = true;
        assignmentTime = timeStampMillis;
    }

    public boolean hasBeenAssignedToAnyUser() {
        return assignedToAnyUser;
    }

    public long getTimePassedUntilFirstAssignment(){
        if (hasBeenAssignedToAnyUser())
            throw new IllegalStateException("getTimePassedUntilFirstAssignment() called on job that has not been assigned");

        return assignmentTime - uploadTime;
    }



}
