package p7gruppe.p7.offloading.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.data.repository.JobRepository;

import java.util.Optional;

public class FIFOJobScheduler implements JobScheduler{


    @Override
    public synchronized Optional<JobEntity> assignJob(DeviceEntity device) {
        return Optional.empty();
    }

    @Override
    public synchronized Optional<JobEntity> assignJob(DeviceEntity device, JobFilter jobFilter) {
        return Optional.empty();
    }
}
