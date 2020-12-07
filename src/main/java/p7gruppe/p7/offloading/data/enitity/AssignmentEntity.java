package p7gruppe.p7.offloading.data.enitity;

import javax.persistence.*;

@Entity
public class AssignmentEntity {

    @Id
    @GeneratedValue
    private Long assignmentId;

    @ManyToOne
    public JobEntity job;

    @ManyToOne
    public DeviceEntity worker;

    @Enumerated(EnumType.STRING)
    public Status status;

    public long timeOfAssignmentInMs;

    public long timeOfCompletionInMs;

    public AssignmentEntity(Status status, DeviceEntity worker, JobEntity job) {
        this.status = status;
        this.worker = worker;
        this.job = job;
        this.timeOfAssignmentInMs = System.currentTimeMillis();
    }

    protected AssignmentEntity() {}

    public enum Status{
        PROCESSING,
        DONE_NOT_CHECKED,
        DONE_MAYBE_WRONG,
        QUIT,
        DONE;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setJob(JobEntity job) {
        this.job = job;
    }

    public void setWorker(DeviceEntity worker) {
        this.worker = worker;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getTimeOfAssignmentInMs() {
        return timeOfAssignmentInMs;
    }

    public long getTimeOfCompletionInMs() {
        return timeOfCompletionInMs;
    }

    public void setTimeOfAssignmentInMs(long assignmentTime) {
        this.timeOfAssignmentInMs = assignmentTime;
    }

    public void setTimeOfCompletionInMs(long completedTime) {
        this.timeOfCompletionInMs = completedTime;
    }

    public Status getStatus() {
        return status;
    }


}
