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
    private Status status;

    public AssignmentEntity(Status status, DeviceEntity worker, JobEntity job) {
        this.status = status;
        this.worker = worker;
        this.job = job;
    }

    protected AssignmentEntity() {}

    public enum Status{
        PROCESSING,
        DONE_NOT_CHECKED,
        DONE_MAYBE_WRONG,
        QUIT,
        DONE;
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
}
