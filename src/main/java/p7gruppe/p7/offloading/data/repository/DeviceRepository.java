package p7gruppe.p7.offloading.data.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;

public interface DeviceRepository extends CrudRepository<DeviceEntity, Long> {
    @Query(value = "SELECT EXISTS (SELECT * FROM device_entity WHERE uuid = ?1)", nativeQuery = true)
    boolean isDevicePresent(String uuid);

    @Query(value = "SELECT EXISTS (SELECT * FROM device_entity WHERE uuid = ?1 AND owner_user_id = ?2)", nativeQuery = true)
    boolean doesDeviceBelongToUser(String uuid, long userId);

    @Query(value = "SELECT * FROM device_entity WHERE uuid = ?1", nativeQuery = true)
    DeviceEntity getDeviceByUUID(String uuid);

    @Query(value = "SELECT AVG(trust_score) FROM device_entity", nativeQuery = true)
    double getAvgTrustScore();
}
