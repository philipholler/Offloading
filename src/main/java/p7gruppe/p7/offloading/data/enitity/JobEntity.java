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
    public int anwsersNeeded;
    public int workersAssigned;
    @Enumerated(EnumType.STRING)
    public JobStatus jobStatus;
    public int timeoutInMinutes;
    public int priority;


    public JobEntity(UserEntity employer, String jobPath, String name, int anwsersNeeded, int timeoutInMinutes) {
        this.employer = employer;
        this.jobPath = jobPath;
        this.name = name;
        this.anwsersNeeded = anwsersNeeded;
        this.workersAssigned = 0;
        this.uploadTime = System.currentTimeMillis();
        this.jobStatus = JobStatus.WAITING;
        this.timeoutInMinutes = timeoutInMinutes;
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
                ", anwsersNeeded=" + anwsersNeeded +
                ", workersAssigned=" + workersAssigned +
                ", jobStatus=" + jobStatus +
                ", timeoutInMinutes=" + timeoutInMinutes +
                ", priority=" + priority +
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
