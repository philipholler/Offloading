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
    public String jobPath;

    public JobEntity(UserEntity employer, String jobPath) {
        this.employer = employer;
        this.jobPath = jobPath;
    }

    protected JobEntity() {}
}
