package p7gruppe.p7.offloading.scheduling;

import org.mapstruct.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.repository.AssignmentRepository;
import p7gruppe.p7.offloading.data.repository.DeviceRepository;
import p7gruppe.p7.offloading.data.repository.JobRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;

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
        return Optional.empty();
    }

    @Override
    public synchronized Optional<JobEntity> assignJob(DeviceEntity device, JobFilter jobFilter) {
        return Optional.empty();
    }
}