package p7gruppe.p7.offloading.performance.mock;

import p7gruppe.p7.offloading.performance.APISupplier;
import p7gruppe.p7.offloading.performance.statistics.ArbitraryCPUDistributionSupplier;
import p7gruppe.p7.offloading.performance.statistics.DistributionSupplier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MockWorkerGenerator {

    private final APISupplier apiSupplier;

    public MockWorkerGenerator(APISupplier apiSupplier) {
        this.apiSupplier = apiSupplier;
    }

    private DistributionSupplier<Double> cpuDistributionSupplier = new ArbitraryCPUDistributionSupplier();

    public List<MockWorker> generateWorkers(int amountOfWorkers, List<MockUser> users, long randomSeed){
        List<Double> cpuTimeFactor = cpuDistributionSupplier.getDistribution(randomSeed, amountOfWorkers);
        List<MockWorker> mockWorkers = new ArrayList<>();

        for (int i = 0; i < amountOfWorkers; i++){
            MockUser user = users.get(i % users.size());
            String deviceID = String.valueOf(i);
            mockWorkers.add(new MockWorker(cpuTimeFactor.get(i), deviceID, user, apiSupplier));
        }

        Collections.shuffle(mockWorkers, new Random(randomSeed));
        return mockWorkers;
    }

    public void setCpuDistributionSupplier(DistributionSupplier<Double> cpuDistributionSupplier) {
        this.cpuDistributionSupplier = cpuDistributionSupplier;
    }

    public List<MockWorker> generateWorkersNotBelongingToUsers(int amountOfWorkers, List<MockUser> users, long randomSeed){
        List<Double> cpuTimeFactor = cpuDistributionSupplier.getDistribution(randomSeed, amountOfWorkers);
        List<MockWorker> mockWorkers = new ArrayList<>();

        for (int i = 0; i < amountOfWorkers; i++){
            MockUser user = users.get(i % users.size());
            String deviceID = String.valueOf(i);
            mockWorkers.add(new MockWorker(cpuTimeFactor.get(i), deviceID, user, apiSupplier));
        }

        Collections.shuffle(mockWorkers, new Random(randomSeed));
        return mockWorkers;
    }
}
