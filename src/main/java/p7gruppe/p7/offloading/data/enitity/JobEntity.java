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

    public JobEntity(UserEntity employer, String jobPath, String name) {
        this.employer = employer;
        this.jobPath = jobPath;
        this.name = name;
        this.uploadTime = System.currentTimeMillis();
    }

    protected JobEntity() {}

    @Override
    public String toString() {
        return "JobEntity{" +
                "jobId=" + jobId +
                ", employer=" + employer +
                ", jobPath='" + jobPath + '\'' +
                '}';
    }

    public Long getJobId() {
        return jobId;
    }

    public String getName() {
        return name;
    }
}
