package p7gruppe.p7.offloading.performance;

import p7gruppe.p7.offloading.data.repository.AssignmentRepository;
import p7gruppe.p7.offloading.data.repository.DeviceRepository;
import p7gruppe.p7.offloading.data.repository.JobRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;

public class RepositorySupplier {

    public final AssignmentRepository assignmentRepository;
    public final JobRepository jobRepository;
    public final UserRepository userRepository;
    public final DeviceRepository deviceRepository;

    public RepositorySupplier(AssignmentRepository assignmentRepository, JobRepository jobRepository, UserRepository userRepository, DeviceRepository deviceRepository) {
        this.assignmentRepository = assignmentRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
    }
}
