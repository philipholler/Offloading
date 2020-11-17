package p7gruppe.p7.offloading.data;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Profile("production")
public class DataConfiguration {

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.postgresql.Driver");
        dataSourceBuilder.url("jdbc:postgresql://localhost:5432/offloading_db");
        dataSourceBuilder.username("postgres");
        dataSourceBuilder.password("Password");
        return dataSourceBuilder.build();
    }

}
