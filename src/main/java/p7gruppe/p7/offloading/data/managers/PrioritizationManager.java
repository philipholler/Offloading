package p7gruppe.p7.offloading.data.managers;

import p7gruppe.p7.offloading.data.enitity.AssignmentEntity;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.data.repository.AssignmentRepository;
import p7gruppe.p7.offloading.data.repository.JobRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;
import p7gruppe.p7.offloading.model.Job;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrioritizationManager {
    UserRepository userRepository;
    JobRepository jobRepository;
    AssignmentRepository assignmentRepository;
    static double workerMultiplyFactor = 0.7;

    public PrioritizationManager(UserRepository userRepository, JobRepository jobRepository, AssignmentRepository assignmentRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.assignmentRepository = assignmentRepository;
    }

    /*

    public void calculateInitialJobPriority(String userName, long jobID) {
        UserEntity userEntity = userRepository.getUserByUsername(userName);
        JobEntity jobEntity = jobRepository.getJobByID(jobID);
        jobEntity.priority = (int) (userEntity.getCpuTime() - jobEntity.timeoutInMinutes * Math.ceil(jobEntity.answersNeeded * workerMultiplyFactor));

        if (jobEntity.priority < 1) {
            jobEntity.priority = 0;
        }
        userEntity.setCpuTime(userEntity.getCpuTime() + jobEntity.priority);
        jobRepository.save(jobEntity);
        userRepository.save(userEntity);

    }*/

    private long getWorkerCreditEarned(long jobID) {
       Optional<Long> assignmentEntity = assignmentRepository.getAverageOfAssignedJobTime(jobID);
        return  assignmentEntity.get();
    }

    /*
    public void calculateUpdatedJobPriority(String userName, long jobID) {
        JobEntity job = jobRepository.getOldestJobByUserName(userName);
        UserEntity userEntity = userRepository.getUserByUsername(userName);
        userEntity.setCpuTime(userEntity.getCpuTime()+ getWorkerCreditEarned(jobID));
        job.priority += getWorkerCreditEarned(jobID);
        userRepository.save(userEntity);
        jobRepository.save(job);
    }*/

}
