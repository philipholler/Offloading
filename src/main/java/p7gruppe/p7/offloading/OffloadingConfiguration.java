package p7gruppe.p7.offloading;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import p7gruppe.p7.offloading.scheduling.FIFOJobScheduler;
import p7gruppe.p7.offloading.scheduling.JobScheduler;

@Configuration
public class OffloadingConfiguration {

    @Bean
    JobScheduler getJobScheduler(){
        return new FIFOJobScheduler();
        // return new ...JobScheduler();
    }

}
