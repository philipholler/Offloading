package p7gruppe.p7.offloading.data.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.model.Job;

import java.util.ArrayList;
import java.util.List;


public interface JobRepository extends CrudRepository<JobEntity, Long> {
    @Query(value = "SELECT * " +
            "FROM job_entity " +
            "WHERE workers_assigned < workers_requested " +
            "ORDER BY upload_time ASC " +
            "LIMIT 1 ",
            nativeQuery = true)
    JobEntity getNewestAvailableJob();

    @Query(value = "SELECT * FROM job_entity INNER JOIN user_entity ue on ue.user_id = job_entity.employer_user_id WHERE user_name = ?1", nativeQuery = true)
    Iterable<JobEntity> getJobsByUsername(String username);
}
