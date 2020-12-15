package p7gruppe.p7.offloading.scheduling;

import p7gruppe.p7.offloading.data.enitity.AssignmentEntity;
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
        boolean shouldContinue = true;

        Optional<AssignmentEntity> assignmentOpt = assignmentRepository.findById(assignmentID);
        // If the assignment is not present, it should not continue
        // This should not possibly happen, but this is a precaution
        if(assignmentOpt.isPresent()){
            AssignmentEntity assignment = assignmentOpt.get();
            JobEntity job = assignment.job;

            // If the job is done, also quit
            if(job.getJobStatus() == JobEntity.JobStatus.DONE
                    || job.getJobStatus() == JobEntity.JobStatus.DONE_CONFLICTING_RESULTS){
                shouldContinue = false;
            }

            // If should not continue, just mark as done
            if(!shouldContinue){
                assignment.setStatus(AssignmentEntity.Status.DONE);
                assignmentRepository.save(assignment);
            }
        } else {
            shouldContinue = false;
        }

        return shouldContinue;
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
