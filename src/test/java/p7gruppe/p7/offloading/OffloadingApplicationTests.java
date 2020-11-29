package p7gruppe.p7.offloading;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import p7gruppe.p7.offloading.api.UsersApiController;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.data.repository.JobRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;
import p7gruppe.p7.offloading.model.UserCredentials;
import p7gruppe.p7.offloading.scheduling.JobScheduler;
import p7gruppe.p7.offloading.scheduling.SampleScheduler;

@SpringBootTest()
class OffloadingApplicationTests {

	@Autowired
	JobRepository jobRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
    UsersApiController usersApiController;

	@Autowired
	JobScheduler scheduler;

	@Test
	void exampleTest() throws Exception {
	    UserCredentials userCredentials = new UserCredentials();
	    userCredentials.setUsername("user1");
	    userCredentials.password("password");
	    usersApiController.createUser(userCredentials);

        //TestUserGeneration.createTestUser(mockMvc);

	    UserEntity user = new UserEntity("SorenSmoke", "password");

        user = userRepository.save(user);

		System.out.println(scheduler instanceof SampleScheduler);
		/*jobRepository.save(new JobEntity(user, "data/test1"));
		jobRepository.save(new JobEntity(user, "data/test2"));


		jobRepository.findAll().forEach((job) -> {
			System.out.println(job.employer.getUserName() + " : " + job.jobPath);
		});

        System.out.println("________");
        JobEntity myJob = jobRepository.getJobsWithId(2L);
        System.out.println(myJob);*/
	}

}
