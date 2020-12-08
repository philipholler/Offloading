package p7gruppe.p7.offloading.scheduling;

import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;

import java.util.Optional;

public interface JobScheduler {
    Optional<JobEntity> assignJob(DeviceEntity device);
    Optional<JobEntity> assignJob(DeviceEntity device, JobFilter jobFilter);
    boolean shouldContinue(long assignmentID);
}
