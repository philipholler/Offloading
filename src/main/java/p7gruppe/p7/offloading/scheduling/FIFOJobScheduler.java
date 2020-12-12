package p7gruppe.p7.offloading.scheduling;

import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.repository.AssignmentRepository;
import p7gruppe.p7.offloading.data.repository.JobRepository;

import java.util.Optional;

public class FIFOJobScheduler implements JobScheduler{
    AssignmentRepository assignmentRepository;
    JobRepository jobRepository;

    public FIFOJobScheduler(AssignmentRepository assignmentRepository,
                            JobRepository jobRepository) {
        this.assignmentRepository = assignmentRepository;
        this.jobRepository = jobRepository;
    }

    @Override
    public synchronized Optional<JobEntity> assignJob(DeviceEntity device) {
        return jobRepository.getOldestAvailableJob();
    }

    @Override
    public synchronized Optional<JobEntity> assignJob(DeviceEntity device, JobFilter jobFilter) {
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
