package p7gruppe.p7.offloading;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.repository.JobRepository;

import javax.sql.DataSource;

@SpringBootTest
class OffloadingApplicationTests {

	@Autowired
	JobRepository jobRepository;

	@Test
	void exampleTest(){
		jobRepository.save(new JobEntity("Mads"));
		jobRepository.save(new JobEntity("Faber"));

		System.out.println(jobRepository.count());
	}

}
