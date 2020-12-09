package p7gruppe.p7.offloading.scheduling;

import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.repository.JobRepository;

import java.util.Optional;

public class InnerCircleScheduler implements JobScheduler {
    JobRepository jobRepository;

    public InnerCircleScheduler(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public Optional<JobEntity> assignJob(DeviceEntity device) {
        JobEntity newJob = jobRepository.getOldestAvailableJob();
        if (newJob != null) return Optional.of(newJob);
        return Optional.empty();
    }

    @Override
    public Optional<JobEntity> assignJob(DeviceEntity device, JobFilter jobFilter) {
        JobEntity newJob = jobRepository.getOldestJobByUserName(device.getOwner().getUserName());
        if (newJob != null) return Optional.of(newJob);
        return Optional.empty();
    }

    @Override
    public boolean shouldContinue(long assignmentID) {
        return true;
    }
}
