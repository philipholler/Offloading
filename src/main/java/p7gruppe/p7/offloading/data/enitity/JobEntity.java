package p7gruppe.p7.offloading.data.enitity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class JobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long jobID;
    private String owner;


    public JobEntity(String owner) {
        this.owner = owner;
    }


    protected JobEntity() {

    }
}
