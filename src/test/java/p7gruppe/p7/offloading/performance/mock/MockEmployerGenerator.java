package p7gruppe.p7.offloading.performance.mock;

import p7gruppe.p7.offloading.performance.APISupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MockEmployerGenerator {

    private final APISupplier apiSupplier;
    private Function<Long, JobSpawner> jobSpawnerSupplier = (randomSeed) -> new RandomIntervalJobSpawner(2000, 2000, randomSeed);

    public MockEmployerGenerator(APISupplier apiSupplier) {
        this.apiSupplier = apiSupplier;
    }

    public void setJobSpawnerSupplier(Function<Long, JobSpawner> jobSpawnerSupplier) {
        this.jobSpawnerSupplier = jobSpawnerSupplier;
    }

    public List<MockEmployer> generateEmployers(int amountOfEmployers, List<MockUser> users, long randomSeed){
        if (amountOfEmployers > users.size())
            throw new IllegalArgumentException("Amount of employers must be <= amount of users");

        List<MockEmployer> employers = new ArrayList<>();
        for (int i = 0; i < amountOfEmployers; i++) {
            JobSpawner jobSpawner = jobSpawnerSupplier.apply(randomSeed + i);
            employers.add(new MockEmployer(users.get(i), apiSupplier, jobSpawner));
        }

        return employers;
    }
}
