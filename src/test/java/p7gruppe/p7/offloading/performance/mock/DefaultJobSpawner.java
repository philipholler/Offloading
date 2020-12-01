package p7gruppe.p7.offloading.performance.mock;

import java.util.Optional;
import java.util.Random;

public class DefaultJobSpawner implements JobSpawner {

    private final int averageJobComputeTime;
    private final int averageJobIntervalMillis;
    private final int maximumJobIntervalDeviationMillis;

    private long nextJobTime;

    private Random random;

    public DefaultJobSpawner(int averageJobComputeTime, long randomSeed) {
        this.averageJobComputeTime = averageJobComputeTime;
        averageJobIntervalMillis = averageJobComputeTime;
        maximumJobIntervalDeviationMillis = averageJobIntervalMillis / 2;

        random = new Random(randomSeed);

        long now = System.currentTimeMillis();
        nextJobTime = now + random.nextInt(averageJobIntervalMillis + maximumJobIntervalDeviationMillis);
    }

    @Override
    public Optional<MockJob> pollJob() {
        if (System.currentTimeMillis() >= nextJobTime)
            return Optional.of(createNewJob());
        return Optional.empty();
    }

    private MockJob createNewJob() {
        int jobComputationTime = averageJobComputeTime + random.nextInt(maximumJobIntervalDeviationMillis);
        return new MockJob(jobComputationTime, 2);
    }
}
