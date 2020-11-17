package p7gruppe.p7.offloading.data.repository;

import org.springframework.data.repository.CrudRepository;
import p7gruppe.p7.offloading.data.enitity.JobEntity;

public interface JobRepository extends CrudRepository<JobEntity, Long> {}
