package p7gruppe.p7.offloading.performance;

import p7gruppe.p7.offloading.performance.statistics.ArbitraryCPUDistributionSupplier;
import p7gruppe.p7.offloading.performance.statistics.DistributionSupplier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MockWorkerGenerator {

    private long randomSeed = 0L;

    private DistributionSupplier<Double> cpuDistributionSupplier = new ArbitraryCPUDistributionSupplier();

    public List<MockWorker> generateWorkers(int amountOfWorkers, List<MockUser> users, APISupplier apiSupplier){
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

    public void setRandomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
    }

    public void setCpuDistributionSupplier(DistributionSupplier<Double> cpuDistributionSupplier) {
        this.cpuDistributionSupplier = cpuDistributionSupplier;
    }
}
