package p7gruppe.p7.offloading.data.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import p7gruppe.p7.offloading.data.enitity.DeviceEntity;

public interface DeviceRepository extends CrudRepository<DeviceEntity, Long> {
    @Query(value = "SELECT EXISTS (SELECT * FROM device_entity WHERE imei = ?1)", nativeQuery = true)
    boolean isDevicePresent(String imei);
}
