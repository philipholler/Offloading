package p7gruppe.p7.offloading.data.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;

public interface DeviceRepository extends CrudRepository<DeviceEntity, Long> {
    @Query(value = "SELECT EXISTS (SELECT * FROM device_entity WHERE imei = ?1)", nativeQuery = true)
    boolean isDevicePresent(String imei);

    @Query(value = "SELECT EXISTS (SELECT * FROM device_entity WHERE imei = ?2 AND user_name = ?1)", nativeQuery = true)
    boolean doesDeviceBelongToUser(String username, String imei);

    @Query(value = "SELECT * FROM device_entity WHERE imei = ?1", nativeQuery = true)
    DeviceEntity getDeviceByIMEI(String imei);
}
