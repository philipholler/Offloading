package p7gruppe.p7.offloading.scheduling;

import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.enitity.JobEntity;

import java.util.Optional;

public class SampleScheduler implements JobScheduler{
    @Override
    public Optional<JobEntity> assignJob(DeviceEntity device) {
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
