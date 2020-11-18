package p7gruppe.p7.offloading.data.enitity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class DeviceEntity {

    @Id
    @GeneratedValue
    private int deviceId;
    private String imei;

    @ManyToOne
    private UserEntity owner;

    public DeviceEntity(UserEntity owner, String imei) {
        this.owner = owner;
        this.imei = imei;
    }

    protected DeviceEntity() {}
}
