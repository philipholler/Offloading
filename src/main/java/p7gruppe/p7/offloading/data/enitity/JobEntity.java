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
    public int timeOut;
    public int priority;
    @Enumerated(EnumType.STRING)
    public JobStatus jobStatus;

    public JobEntity(UserEntity employer, String jobPath, String name, int workersRequested, int timeOut) {
        this.employer = employer;
        this.jobPath = jobPath;
        this.name = name;
        this.workersRequested = workersRequested;
        this.workersAssigned = 0;
        this.uploadTime = System.currentTimeMillis();
        this.jobStatus = JobStatus.WAITING;
        this.timeOut = timeOut;
    }

    protected JobEntity() {
    }

    @Override
    public String toString() {
        return "JobEntity{" +
                "jobId=" + jobId +
                ", employer=" + employer +
                ", name='" + name + '\'' +
                ", jobPath='" + jobPath + '\'' +
                ", uploadTime=" + uploadTime +
                ", workersRequested=" + workersRequested +
                ", workersAssigned=" + workersAssigned +
                ", timeOut=" + timeOut +
                ", priority=" + priority +
                ", jobStatus=" + jobStatus +
                '}';
    }

    public Long getJobId() {
        return jobId;
    }

    public String getName() {
        return name;
    }

    public enum JobStatus {
        WAITING,
        PROCESSING,
        DONE
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

}
