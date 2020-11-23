package p7gruppe.p7.offloading.data.enitity;

import javax.persistence.*;

@Entity
public class JobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long jobId;

    @ManyToOne
    public UserEntity employer;

    private String name;
    public String jobPath;
    public long uploadTime;
    public int workersRequested;
    public int workersAssigned;
    @Enumerated(EnumType.STRING)
    public JobStatus jobStatus;

    public JobEntity(UserEntity employer, String jobPath, String name, int workersRequested) {
        this.employer = employer;
        this.jobPath = jobPath;
        this.name = name;
        this.workersRequested = workersRequested;
        this.workersAssigned = 0;
        this.uploadTime = System.currentTimeMillis();
        this.jobStatus = JobStatus.WAITING;
    }

    protected JobEntity() {}

    @Override
    public String toString() {
        return "JobEntity{" +
                "jobId=" + jobId +
                ", employer=" + employer +
                ", name='" + name + '\'' +
                ", jobPath='" + jobPath + '\'' +
                ", uploadTime=" + uploadTime +
                ", requestedWorkers=" + workersRequested +
                ", workersAssigned=" + workersAssigned +
                '}';
    }

    public Long getJobId() {
        return jobId;
    }

    public String getName() {
        return name;
    }

    public enum JobStatus{
        WAITING,
        PROCESSING,
        DONE
    }
}
