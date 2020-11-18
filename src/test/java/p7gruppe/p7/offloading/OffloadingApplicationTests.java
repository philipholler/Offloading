package p7gruppe.p7.offloading;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.data.repository.JobRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;

import javax.sql.DataSource;

@SpringBootTest
class OffloadingApplicationTests {

	@Autowired
	JobRepository jobRepository;

	@Autowired
	UserRepository userRepository;

	@Test
	void exampleTest(){
		UserEntity user = userRepository.save(new UserEntity("SorenSmoke", "password"));

		jobRepository.save(new JobEntity(user, "data/test1"));
		jobRepository.save(new JobEntity(user, "data/test2"));
		
		jobRepository.findAll().forEach((job) -> {
			System.out.println(job.employer.getUserName() + " : " + job.jobPath);
		});
	}

}
