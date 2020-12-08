package p7gruppe.p7.offloading.data.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import p7gruppe.p7.offloading.data.enitity.AssignmentEntity;
import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.model.DeviceId;

import java.util.Optional;

public interface AssignmentRepository extends CrudRepository<AssignmentEntity, Long> {

    @Query(value = "SELECT * FROM assignment_entity WHERE worker_device_id = ?1 AND status = 'PROCESSING'", nativeQuery = true)
    Optional<AssignmentEntity> getProcessingAssignmentForDevice(long workerDeviceID);

    @Query(value = "SELECT avg(time_of_completion - time_of_assignment) as workertime FROM assignment_entity WHERE job_job_id = ?1 ", nativeQuery = true)
    Optional<Long> getAverageOfAssignedJobTime(long jobID);

    @Query(value = "SELECT * FROM assignment_entity WHERE job_job_id = ?1", nativeQuery = true)
    Iterable<AssignmentEntity> getAssignmentForJob(Long jobId);

    @Query(value = "SELECT COUNT FROM assignment_entity WHERE job_job_id = ?1", nativeQuery = true)
    Integer getNumberOfAssignmentsForJob(long jobId);

    @Query(value = "SELECT COUNT FROM assignment_entity WHERE job_job_id = ?1 AND worker = ?2", nativeQuery = true)
    AssignmentEntity getAssignmentForJob(Long jobId, long deviceId);
}
