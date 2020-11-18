package p7gruppe.p7.offloading.data.enitity;

import org.springframework.beans.factory.annotation.Autowired;

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

    public JobEntity(UserEntity employer, String jobPath, String name) {
        this.employer = employer;
        this.jobPath = jobPath;
        this.name = name;
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
