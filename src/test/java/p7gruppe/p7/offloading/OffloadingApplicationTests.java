package p7gruppe.p7.offloading;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@SpringBootTest
class OffloadingApplicationTests {

	@Autowired
	DataSource dataSource;

	@Test
	void exampleTest(){
		JdbcTemplate temp = new JdbcTemplate(dataSource);
		temp.execute("INSERT INTO client VALUES (5)");
	}

}
