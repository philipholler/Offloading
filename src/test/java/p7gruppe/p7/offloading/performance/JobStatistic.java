package p7gruppe.p7.offloading.performance;

import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.performance.mock.MockUser;

public class JobStatistic {

    public final String jobName;

    private long uploadTime = 0L;
    private long finishTime = -1L;
    private long statisticEndTime = -1L;
    private boolean hasFinished = false;

    private final int expectedCPUTime;
    public final MockUser user;
    private boolean resultCorrect = true;
    private JobEntity.JobStatus status = null;

    private boolean assignedToAnyUser = false;
    private long assignmentTime = 0L;

    public final long jobID;

    public JobStatistic(String jobName, int expectedCPUTime, MockUser user) {
        this.jobName = jobName;
        this.expectedCPUTime = expectedCPUTime;
        this.user = user;
    }

    public void registerStatus(JobEntity.JobStatus newStatus, long timeStampMillis){
        if (status != null && status.equals(newStatus)) return; // no update necessary

        switch (newStatus) {
            case WAITING:
                registerUpload(timeStampMillis);
                break;
            case PROCESSING:
                registerAssignment(timeStampMillis);
                break;
            case DONE:
            case DONE_CONFLICTING_RESULTS:
                registerAsFinished(timeStampMillis);
                break;
            default:
                throw new RuntimeException("No logic for handling job status update: "  + newStatus.name());
        }

        status = newStatus;
    }

    public void registerUpload(long uploadTime){
        this.uploadTime = uploadTime;
        status = JobEntity.JobStatus.WAITING;
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

    public boolean isJobCompleted() {
        return hasFinished;
    }
}
