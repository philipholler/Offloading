package p7gruppe.p7.offloading.data.enitity;

import javax.persistence.*;

@Entity
public class AssignmentEntity {

    @Id
    @GeneratedValue
    private Long assignmentId;

    @OneToOne
    private UserEntity worker;
    private String status;

    public AssignmentEntity(String status) {
        this.status = status;
    }

    protected AssignmentEntity() {}
}
