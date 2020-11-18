package p7gruppe.p7.offloading.data.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.model.Job;

import java.util.List;


public interface JobRepository extends CrudRepository<JobEntity, Long> {
    @Query (value = "SELECT * FROM job_entity WHERE job_id = ?1", nativeQuery = true)
    JobEntity getJobsWithId(Long name);
}
