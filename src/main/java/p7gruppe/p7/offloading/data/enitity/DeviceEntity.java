package p7gruppe.p7.offloading.data.enitity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class DeviceEntity {

    @Id
    @GeneratedValue
    public int deviceId;
    private String uuid;

    @ManyToOne
    private UserEntity owner;
    private int assignmentsFinished;
    private int assignmentsFinishedCorrectResult;
    public double trustScore;

    public DeviceEntity(UserEntity owner, String uuid) {
        this.owner = owner;
        this.uuid = uuid;
        this.assignmentsFinished = 0;
        this.assignmentsFinishedCorrectResult = 0;
        this.trustScore = 0.5;
    }

    public UserEntity getOwner() {
        return owner;
    }

    protected DeviceEntity() {}

    public int getDeviceId() {
        return deviceId;
    }

    public String getUuid() {
        return uuid;
    }

    public void incrementAssignmentsFinished(){
        this.assignmentsFinished++;
    }

    public void incrementAssignmentsFinishedCorrectResult(){
        this.assignmentsFinishedCorrectResult++;
    }

    public void updateTrustScore(boolean correctResult){
        if(correctResult){
            this.trustScore = Math.min(1.0, this.trustScore + 0.05);
        }
        else {
            this.trustScore = this.trustScore / 2;
        }
    }
}
