package p7gruppe.p7.offloading;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.data.repository.JobRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;

@SpringBootTest
class OffloadingApplicationTests {

	@Autowired
	JobRepository jobRepository;

	@Autowired
	UserRepository userRepository;

	@Test
	void exampleTest(){
	    UserEntity user = new UserEntity("SorenSmoke", "password");

        user = userRepository.save(user);

		jobRepository.save(new JobEntity(user, "data/test1"));
		jobRepository.save(new JobEntity(user, "data/test2"));


		jobRepository.findAll().forEach((job) -> {
			System.out.println(job.employer.getUserName() + " : " + job.jobPath);
		});

        System.out.println("________");
        JobEntity myJob = jobRepository.getJobsWithId(2L);
        System.out.println(myJob);
	}

}
