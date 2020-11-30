package p7gruppe.p7.offloading.performance;

import p7gruppe.p7.offloading.api.AssignmentsApi;
import p7gruppe.p7.offloading.api.JobsApi;
import p7gruppe.p7.offloading.api.UsersApi;

public class APISupplier {

    public final UsersApi usersApi;
    public final AssignmentsApi assignmentsApi;
    public final JobsApi jobsApi;

    public APISupplier(UsersApi usersApi, AssignmentsApi assignmentsApi, JobsApi jobsApi) {
        this.usersApi = usersApi;
        this.assignmentsApi = assignmentsApi;
        this.jobsApi = jobsApi;
    }
}
