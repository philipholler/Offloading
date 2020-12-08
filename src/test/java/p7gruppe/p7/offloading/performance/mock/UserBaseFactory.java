package p7gruppe.p7.offloading.performance.mock;

import p7gruppe.p7.offloading.performance.APISupplier;

import java.util.List;

public class UserBaseFactory {

    private final APISupplier apiSupplier;

    public UserBaseFactory(APISupplier apiSupplier) {
        this.apiSupplier = apiSupplier;
    }

    public UserBase generateDefaultUserBase(long randomSeed, int userCount, int workerCount, int employerCount){
        if (employerCount > userCount)
            throw new IllegalArgumentException("Cannot generate more employers than users");

        MockUserGenerator userGenerator = new MockUserGenerator(apiSupplier);
        userGenerator.setProportionOfMaliciousUsers(0.1);
        MockEmployerGenerator employerGenerator = new MockEmployerGenerator(apiSupplier);
        MockWorkerGenerator workerGenerator = new MockWorkerGenerator(apiSupplier);

        List<MockUser> users = userGenerator.generateUsers(userCount, randomSeed);
        List<MockEmployer> employers = employerGenerator.generateEmployers(employerCount, users, randomSeed);
        List<MockWorker> workers = workerGenerator.generateWorkers(workerCount, users, randomSeed);

        return new UserBase(users, employers, workers);
    }


}
