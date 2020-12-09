package p7gruppe.p7.offloading.scheduling;

import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.repository.JobRepository;

import java.util.Optional;

public class EconomicScheduler implements JobScheduler {
    JobRepository jobRepository;

    @Override
    public Optional<JobEntity> assignJob(DeviceEntity device) {
        JobEntity newJob = jobRepository.getOldestAvailableJobFromSameUser(device.getOwner().getUserName());

        if (newJob == null)
            newJob = jobRepository.getJobWithHighestUserPriority();

        if (newJob != null) return Optional.of(newJob);
        return Optional.empty();
    }

    @Override
    public Optional<JobEntity> assignJob(DeviceEntity device, JobFilter jobFilter) {
        return Optional.empty();
    }

    @Override
    public boolean shouldContinue(long assignmentID) {
        return true;
    }

    @Override
    public boolean usingTestAssignments() {
        return false;
    }

    @Override
    public boolean shouldTrustDevice(DeviceEntity device) {
        return true;
    }
}
