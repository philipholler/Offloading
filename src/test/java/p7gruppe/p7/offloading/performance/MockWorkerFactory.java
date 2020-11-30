package p7gruppe.p7.offloading.performance;

import p7gruppe.p7.offloading.performance.statistics.DistributionSupplier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MockWorkerFactory {

    private long randomSeed = 0L;
    private float fractionMalicious = 0.0f;

    private DistributionSupplier<Float> cpuDistributionSupplier;

    public List<MockWorker> createWorkers(int amountOfWorkers){
        int amountOfMaliciousWorkers = Math.round(amountOfWorkers * fractionMalicious);
        List<Float> cpuTimeFactors = cpuDistributionSupplier.getDistribution(randomSeed, amountOfWorkers);

        List<MockWorker> mockWorkers = new ArrayList<>();
        for (int i = 0; i < amountOfWorkers; i++){
            String deviceID = String.valueOf(i);
            boolean isMalicious = i < amountOfMaliciousWorkers;
            mockWorkers.add(new MockWorker(cpuTimeFactors.get(i), isMalicious, deviceID));
        }

        Collections.shuffle(mockWorkers, new Random(randomSeed));
        return mockWorkers;
    }

    public void setFractionMalicious(float fractionMalicious) {
        if (fractionMalicious > 1.0f || fractionMalicious < 0.0f)
            throw new IllegalArgumentException("Fraction of malicious users must be between 0.0 and 1.0");
        this.fractionMalicious = fractionMalicious;
    }

    public void setRandomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
    }

    public void setCpuDistributionSupplier(DistributionSupplier<Float> cpuDistributionSupplier) {
        this.cpuDistributionSupplier = cpuDistributionSupplier;
    }
}
