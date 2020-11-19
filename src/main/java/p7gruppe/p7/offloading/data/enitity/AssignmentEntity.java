package p7gruppe.p7.offloading.data.enitity;

import javax.persistence.*;

@Entity
public class AssignmentEntity {

    @Id
    @GeneratedValue
    private Long assignmentId;

    @ManyToOne
    private JobEntity job;

    @ManyToOne
    private UserEntity worker;

    @Enumerated(EnumType.STRING)
    private Status status;

    public AssignmentEntity(Status status, UserEntity worker, JobEntity job) {
        this.status = status;
        this.worker = worker;
        this.job = job;
    }

    protected AssignmentEntity() {}

    public enum Status{
        PROCESSING,
        DONE_NOT_CHECKED,
        DONE_MAYBE_WRONG,
        DONE;
    }
}
