package p7gruppe.p7.offloading.data.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.model.Job;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public interface JobRepository extends CrudRepository<JobEntity, Long> {
    @Query(value = "SELECT * " +
            "FROM job_entity " +
            "WHERE workers_assigned < answers_needed " +
            "ORDER BY upload_time ASC " +
            "LIMIT 1 ",
            nativeQuery = true)
    JobEntity getOldestAvailableJob();

    @Query(value = "SELECT * FROM job_entity INNER JOIN user_entity ue on ue.user_id = job_entity.employer_user_id WHERE user_name = ?1", nativeQuery = true)
    Iterable<JobEntity> getJobsByUsername(String username);

    @Query(value = "SELECT * " +
            "FROM job_entity " +
            "         INNER JOIN user_entity ue on ue.user_id = job_entity.employer_user_id " +
            "WHERE user_name = ?1 AND workers_assigned < answers_needed " +
            "ORDER BY upload_time ASC " +
            "limit 1 ",
            nativeQuery = true)
    Optional<JobEntity> getOldestAvailableJobFromSameUser(String userName);

    @Query(value = "SELECT * " +
                    "FROM job_entity INNER JOIN user_entity ue on ue.user_id = job_entity.employer_user_id " +
                    "WHERE job_entity.workers_assigned < job_entity.answers_needed " +
                    "ORDER BY  cpu_time_contributed_in_ms-cpu_time_spent_in_ms DESC, upload_time ASC LIMIT 1 ",
            nativeQuery = true)
    Optional<JobEntity> getJobWithHighestUserPriority();

    @Query(value = "SELECT job_id, answers_needed, confidence_level, job_path, job_status, name, priority, timeout_in_minutes, upload_time, workers_assigned, employer_user_id " +
            "FROM job_entity INNER JOIN user_entity ue on ue.user_id = job_entity.employer_user_id " +
            "WHERE job_entity.workers_assigned < job_entity.answers_needed " +
            "        AND ?1 != ue.user_id " +
            "ORDER BY cpu_time_contributed_in_ms-cpu_time_spent_in_ms DESC, upload_time ASC LIMIT 1 ",
            nativeQuery = true)
    Optional<JobEntity> getJobWithHighestUserPriorityFromOtherUser(long userId);

    @Query(value = "SELECT priority, timeout_in_minutes FROM job_entity WHERE employer_user_id = ?1",
            nativeQuery = true)
    Iterable<JobEntity> getJobPriorityAndTimeOutByUserID();

    @Query(value = "SELECT * FROM job_entity WHERE job_id = ?1 LIMIT 1",
            nativeQuery = true)
    JobEntity getJobByID(long jobID);


    @Query(value = "SELECT * FROM job_entity\n" +
            "INNER JOIN user_entity ue on ue.user_id = job_entity.employer_user_id\n" +
            "WHERE user_name = ?1 ORDER BY  upload_time LIMIT 1",
            nativeQuery = true)
    JobEntity getOldestJobByUserName(String userName);


}
