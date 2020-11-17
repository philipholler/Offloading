package p7gruppe.p7.offloading.data.enitity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class DeviceEntity {

    @Id
    @GeneratedValue
    private Long deviceId;

    @ManyToOne
    private UserEntity owner;

    public DeviceEntity(UserEntity owner) {
        this.owner = owner;
    }

    protected DeviceEntity() {}
}
