package p7gruppe.p7.offloading.performance.mock;

import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.data.repository.UserRepository;
import p7gruppe.p7.offloading.performance.APISupplier;
import p7gruppe.p7.offloading.performance.RepositorySupplier;
import p7gruppe.p7.offloading.performance.statistics.DistributionSupplier;

import java.util.ArrayList;
import java.util.List;

public class UserBaseFactory {

    private final APISupplier apiSupplier;
    private final RepositorySupplier repositorySupplier;

    public UserBaseFactory(APISupplier apiSupplier, RepositorySupplier repositorySupplier) {
        this.apiSupplier = apiSupplier;
        this.repositorySupplier = repositorySupplier;
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

    public UserBase generateUserBaseSomeUsersWithoutWorkers(long randomSeed, int userCount, int workerCount, int employerCount){
        if (employerCount > userCount)
            throw new IllegalArgumentException("Cannot generate more employers than users");

        MockUserGenerator userGenerator = new MockUserGenerator(apiSupplier);
        userGenerator.setProportionOfMaliciousUsers(0.1);
        MockEmployerGenerator employerGenerator = new MockEmployerGenerator(apiSupplier);
        employerGenerator.setJobSpawnerSupplier((seed) -> {
            RandomIntervalJobSpawner jobSpawner= new RandomIntervalJobSpawner(10000, 10000, seed);
            jobSpawner.setMaximumComputeTimeDeviationMillis(0);
            jobSpawner.setMaximumSpawnIntervalDeviationMillis(3500);
            return jobSpawner;
        });
        MockWorkerGenerator workerGenerator = new MockWorkerGenerator(apiSupplier);

        List<MockUser> users = userGenerator.generateUsers(userCount, randomSeed);
        List<MockUser> usersWithoutWorkers = userGenerator.generateUsers(userCount, randomSeed);
        List<MockEmployer> employers = employerGenerator.generateEmployers(employerCount, users, randomSeed);
        List<MockWorker> workers = workerGenerator.generateWorkers(workerCount, users, randomSeed);

        users.addAll(usersWithoutWorkers);
        return new UserBase(users, employers, workers);
    }

    public UserBase generateBankedTimeTestUserBase(long randomSeed, int workerCount, int employerCount){
        MockUserGenerator userGenerator = new MockUserGenerator(apiSupplier);
        List<MockUser> employerUsers = userGenerator.generateUsers(employerCount, randomSeed);
        userGenerator.setProportionOfMaliciousUsers(0.1);
        List<MockUser> workerUsers = userGenerator.generateUsers(workerCount, randomSeed);

        MockEmployerGenerator employerGenerator = new MockEmployerGenerator(apiSupplier);
        employerGenerator.setJobSpawnerSupplier((seed) -> {
            RandomIntervalJobSpawner jobSpawner= new RandomIntervalJobSpawner(4000, 10000, seed);
            jobSpawner.setMaximumComputeTimeDeviationMillis(0);
            jobSpawner.setMaximumSpawnIntervalDeviationMillis(3500);
            return jobSpawner;
        });
        MockWorkerGenerator workerGenerator = new MockWorkerGenerator(apiSupplier);
        /*workerGenerator.setCpuDistributionSupplier(new DistributionSupplier<Double>() {
            @Override // All devices are equally fast
            public List<Double> getDistribution(long randomSeed, int points) {
                List<Double> val = new ArrayList<>();
                for (int i = 0; i < points; i++) val.add(1.0);
                return val;
            }
        }); */
        // todo :  add this in if needed (will cause all workers to have same cpu speed)

        List<MockEmployer> employers = employerGenerator.generateEmployers(employerCount, employerUsers, randomSeed);
        List<MockWorker> workers = workerGenerator.generateWorkers(workerCount, workerUsers, randomSeed);

        final long bankedTimeStepSizeMillis = 10L * 1000L;
        UserRepository userRepository = repositorySupplier.userRepository;
        for (int i = 0;  i < employerCount / 2; i++ ) {
            MockUser employer = employerUsers.get(i);
            final long bankedTime = i * bankedTimeStepSizeMillis;
            employer.setOnRegistered(() -> {
                UserEntity userEntity = userRepository.getUserByUsername(employer.userCredentials.getUsername());
                userEntity.setCpuTimeSpentInMs(bankedTimeStepSizeMillis * bankedTime);
                userRepository.save(userEntity);
            });
        }

        List<MockUser> allUsers = new ArrayList<>();
        return new UserBase(allUsers, employers, workers);
    }



    /*
    public UserBase generateBankTimeUserBase(){
        MockUserGenerator userGenerator = new MockUserGenerator(apiSupplier);
        userGenerator.setProportionOfMaliciousUsers(0.1);
        MockEmployerGenerator employerGenerator = new MockEmployerGenerator(apiSupplier);
        MockWorkerGenerator workerGenerator = new MockWorkerGenerator(apiSupplier);

    }*/


}
