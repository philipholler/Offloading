package p7gruppe.p7.offloading.scheduling;

import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.repository.AssignmentRepository;
import p7gruppe.p7.offloading.data.repository.JobRepository;

import java.util.Optional;

public class InnerCircleScheduler implements JobScheduler {
    JobRepository jobRepository;

    public InnerCircleScheduler(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public Optional<JobEntity> assignJob(DeviceEntity device) {
        JobEntity newJob = jobRepository.getNewestAvailableJob();
        if (newJob != null) return Optional.of(newJob);
        return Optional.empty();
    }

    @Override
    public Optional<JobEntity> assignJob(DeviceEntity device, JobFilter jobFilter) {
        JobEntity newJob = jobRepository.getNewestAvailableJobFromSameUser();
        if (newJob != null) return Optional.of(newJob);
        return Optional.empty();
    }
}
