package p7gruppe.p7.offloading.data.managers;

import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.data.repository.JobRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;

public class PrioritizationManager {
     UserRepository userRepository;
     JobRepository jobRepository;

    static double workerMultiplyFactor = 0.7;

    public PrioritizationManager(UserRepository userRepository, JobRepository jobRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public void calculateJobPriority(String userName, long jobID) {

        UserEntity userEntity = userRepository.getUserByUsername(userName);
        JobEntity jobEntity = jobRepository.getJobByID(jobID);

        jobEntity.priority = (int) (userEntity.cpuTime - jobEntity.timeoutInMinutes * Math.ceil(jobEntity.answersNeeded * workerMultiplyFactor));

        if (jobEntity.priority < 1) {
            jobEntity.priority = 0;
        }
        userEntity.cpuTime = jobEntity.priority;
        jobRepository.save(jobEntity);
        userRepository.save(userEntity);

    }

}
