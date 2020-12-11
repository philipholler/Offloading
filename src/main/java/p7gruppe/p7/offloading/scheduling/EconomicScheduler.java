package p7gruppe.p7.offloading.scheduling;

import p7gruppe.p7.offloading.data.enitity.AssignmentEntity;
import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.repository.AssignmentRepository;
import p7gruppe.p7.offloading.data.repository.DeviceRepository;
import p7gruppe.p7.offloading.data.repository.JobRepository;

import java.util.Optional;

public class EconomicScheduler implements JobScheduler {
    JobRepository jobRepository;
    AssignmentRepository assignmentRepository;
    DeviceRepository deviceRepository;
    boolean usingTrust;

    public EconomicScheduler(JobRepository jobRepository, AssignmentRepository assignmentRepository,
                             DeviceRepository deviceRepository, boolean usingTrust) {
        this.jobRepository = jobRepository;
        this.assignmentRepository = assignmentRepository;
        this.deviceRepository = deviceRepository;
        this.usingTrust = usingTrust;
    }

    @Override
    public Optional<JobEntity> assignJob(DeviceEntity device) {
        Optional<JobEntity> jobFromSameUser = jobRepository.getOldestAvailableJobFromSameUser(device.getOwner().getUserName());

        if(jobFromSameUser.isPresent()){
            return jobFromSameUser;
        }

        // Look for other users jobs, that have the highest possible priority
        Optional<JobEntity> jobFromOtherUsers = jobRepository.getJobWithHighestUserPriorityFromOtherUser(device.getOwner().getUserId());

        return jobFromOtherUsers;
    }

    @Override
    public Optional<JobEntity> assignJob(DeviceEntity device, JobFilter jobFilter) {
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
        return this.usingTrust;
    }

    @Override
    public boolean shouldTrustDevice(DeviceEntity device) {
        // If not using trust, trust anyone
        if(!usingTrust){
            return true;
        }

        double allDevicesAvgTrustScore = deviceRepository.getAvgTrustScore();

        if(device.trustScore < 0.4) return false;
        return !(device.trustScore < allDevicesAvgTrustScore / 2);
    }
}
