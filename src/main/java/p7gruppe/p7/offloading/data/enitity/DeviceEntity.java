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
    private String imei;

    @ManyToOne
    private UserEntity owner;
    private int assignmentsFinished;
    private int assignmentsFinishedCorrectResult;

    public DeviceEntity(UserEntity owner, String imei) {
        this.owner = owner;
        this.imei = imei;
        this.assignmentsFinished = 0;
        this.assignmentsFinishedCorrectResult = 0;
    }

    public UserEntity getOwner() {
        return owner;
    }

    protected DeviceEntity() {}

    public int getDeviceId() {
        return deviceId;
    }

    public String getImei() {
        return imei;
    }

    @Override
    public String toString() {
        return "DeviceEntity{" +
                "deviceId=" + deviceId +
                ", imei='" + imei + '\'' +
                ", owner=" + owner +
                ", assignmentsFinished=" + assignmentsFinished +
                ", assignmentsFinishedCorrectResult=" + assignmentsFinishedCorrectResult +
                '}';
    }

    public void incrementAssignmentsFinished(){
        this.assignmentsFinished++;
    }

    public void incrementAssignmentsFinishedCorrectResult(){
        this.assignmentsFinishedCorrectResult++;
    }
}
