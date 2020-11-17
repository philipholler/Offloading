package p7gruppe.p7.offloading;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import p7gruppe.p7.offloading.scheduling.FIFOJobScheduler;
import p7gruppe.p7.offloading.scheduling.JobScheduler;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    @Scope("singleton")
    JobScheduler getJobScheduler(){
        return new FIFOJobScheduler();
        // return new ...JobScheduler();
    }

}
